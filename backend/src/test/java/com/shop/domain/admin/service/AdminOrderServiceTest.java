package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.inventory.entity.InventoryType;
import com.shop.domain.inventory.service.InventoryService;
import com.shop.domain.order.entity.*;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.order.repository.OrderShippingRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import com.shop.global.service.EmailService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderShippingRepository orderShippingRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdminOrderService adminOrderService;

    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() throws Exception {
        // self-injection is not handled by @InjectMocks; wire it manually so that
        // bulkUpdateShipping can delegate to processBulkShippingRow via the proxy field
        Field selfField = AdminOrderService.class.getDeclaredField("self");
        selfField.setAccessible(true);
        selfField.set(adminOrderService, adminOrderService);

        testOrder = Order.builder()
                .memberId(1L)
                .orderNumber("ORD-20260313-001")
                .receiverName("홍길동")
                .receiverPhone("010-1234-5678")
                .zipcode("12345")
                .address("서울시 강남구")
                .addressDetail("101호")
                .deliveryMemo("부재시 문 앞")
                .totalPrice(50000)
                .couponDiscount(0)
                .pointDiscount(0)
                .shippingFee(3000)
                .finalPrice(53000)
                .build();

        setId(testOrder, 1L);

        testOrderItem = OrderItem.builder()
                .order(testOrder)
                .productId(10L)
                .optionId(null)
                .productName("테스트 상품")
                .optionInfo("옵션 없음")
                .qty(2)
                .price(25000)
                .build();

        setId(testOrderItem, 100L);
        testOrder.addItem(testOrderItem);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field field = getFieldFromHierarchy(entity.getClass(), "id");
        field.setAccessible(true);
        field.set(entity, id);
    }

    private Field getFieldFromHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getFieldFromHierarchy(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }

    @Test
    void 주문_상태_변경_성공() {
        // given
        given(orderRepository.findByIdWithItems(1L)).willReturn(Optional.of(testOrder));

        // when
        adminOrderService.updateOrderStatus(1L, OrderStatus.PREPARING);

        // then
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.PREPARING);
        verify(orderRepository).findByIdWithItems(1L);
    }

    @Test
    void 주문_상태_변경_없는_주문_실패() {
        // given
        given(orderRepository.findByIdWithItems(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminOrderService.updateOrderStatus(999L, OrderStatus.PREPARING))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    void 주문_취소_시_재고_복원() {
        // given
        given(orderRepository.findByIdWithItems(1L)).willReturn(Optional.of(testOrder));

        // when
        adminOrderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        // then
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(inventoryService).increase(10L, null, 2, InventoryType.CANCEL, 1L);
    }

    @Test
    void 배송_처리_성공() {
        // given
        AdminShippingUpdateRequest request = new AdminShippingUpdateRequest();
        setFieldValue(request, "courier", "CJ대한통운");
        setFieldValue(request, "trackingNumber", "123456789");

        given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));
        given(orderShippingRepository.findByOrderId(1L)).willReturn(Optional.empty());
        given(orderShippingRepository.save(any(OrderShipping.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminOrderService.updateShipping(1L, request);

        // then
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        verify(orderShippingRepository).save(any(OrderShipping.class));
        verify(emailService).sendShippingNotification(testOrder);
    }

    @Test
    void 배송_처리_기존_송장_업데이트() {
        // given
        AdminShippingUpdateRequest request = new AdminShippingUpdateRequest();
        setFieldValue(request, "courier", "한진택배");
        setFieldValue(request, "trackingNumber", "987654321");

        OrderShipping existingShipping = OrderShipping.builder()
                .orderId(1L)
                .courier("CJ대한통운")
                .trackingNumber("111111111")
                .build();

        given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));
        given(orderShippingRepository.findByOrderId(1L)).willReturn(Optional.of(existingShipping));
        given(orderShippingRepository.save(any(OrderShipping.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminOrderService.updateShipping(1L, request);

        // then
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(existingShipping.getCourier()).isEqualTo("한진택배");
        assertThat(existingShipping.getTrackingNumber()).isEqualTo("987654321");
        assertThat(existingShipping.getShippedAt()).isNotNull();
        verify(orderShippingRepository).save(existingShipping);
        verify(emailService).sendShippingNotification(testOrder);
    }

    @Test
    void 반품_처리_성공() throws Exception {
        // given
        testOrder.updateStatus(OrderStatus.REFUND_REQUESTED);

        AdminRefundRequest request = new AdminRefundRequest();
        setFieldValue(request, "orderItemIds", List.of(100L));

        given(orderRepository.findByIdWithItems(1L)).willReturn(Optional.of(testOrder));

        // when
        adminOrderService.processRefund(1L, request);

        // then
        assertThat(testOrderItem.getStatus()).isEqualTo(OrderItemStatus.RETURNED);
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.REFUNDED);
        verify(inventoryService).increase(10L, null, 2, InventoryType.CANCEL, 1L);
        verify(emailService).sendRefundNotification(testOrder);
    }

    @Test
    void 엑셀_일괄_송장_업로드_성공() throws Exception {
        // given: create an in-memory XSSFWorkbook with header + 3 data rows
        XSSFWorkbook workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("shipping");

        // header row
        var headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("주문번호");
        headerRow.createCell(1).setCellValue("택배사");
        headerRow.createCell(2).setCellValue("송장번호");

        // data rows
        String[][] data = {
            {"ORD-001", "CJ대한통운", "111"},
            {"ORD-002", "한진택배", "222"},
            {"ORD-003", "우체국택배", "333"}
        };

        for (int i = 0; i < data.length; i++) {
            var row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(data[i][0]);
            row.createCell(1).setCellValue(data[i][1]);
            row.createCell(2).setCellValue(data[i][2]);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file", "shipping.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray());

        // stub orders
        Order order1 = Order.builder().memberId(1L).orderNumber("ORD-001").receiverName("홍길동")
                .receiverPhone("010-0000-0001").zipcode("11111").address("주소1").finalPrice(10000)
                .totalPrice(10000).couponDiscount(0).pointDiscount(0).shippingFee(0).build();
        setId(order1, 10L);

        Order order2 = Order.builder().memberId(1L).orderNumber("ORD-002").receiverName("김철수")
                .receiverPhone("010-0000-0002").zipcode("22222").address("주소2").finalPrice(20000)
                .totalPrice(20000).couponDiscount(0).pointDiscount(0).shippingFee(0).build();
        setId(order2, 20L);

        Order order3 = Order.builder().memberId(1L).orderNumber("ORD-003").receiverName("이영희")
                .receiverPhone("010-0000-0003").zipcode("33333").address("주소3").finalPrice(30000)
                .totalPrice(30000).couponDiscount(0).pointDiscount(0).shippingFee(0).build();
        setId(order3, 30L);

        given(orderRepository.findByOrderNumber("ORD-001")).willReturn(Optional.of(order1));
        given(orderRepository.findByOrderNumber("ORD-002")).willReturn(Optional.of(order2));
        given(orderRepository.findByOrderNumber("ORD-003")).willReturn(Optional.of(order3));

        given(orderShippingRepository.findByOrderId(anyLong())).willReturn(Optional.empty());
        given(orderShippingRepository.save(any(OrderShipping.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminOrderService.bulkUpdateShipping(file);

        // then
        assertThat(order1.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(order2.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(order3.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        verify(orderShippingRepository, times(3)).save(any(OrderShipping.class));
        verify(emailService, times(3)).sendShippingNotification(any(Order.class));
    }

    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
