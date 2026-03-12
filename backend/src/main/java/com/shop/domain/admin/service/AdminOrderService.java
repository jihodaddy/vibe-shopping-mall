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
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderShippingRepository orderShippingRepository;
    private final InventoryService inventoryService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public Page<AdminOrderResponse> getOrderList(AdminOrderSearchCondition condition, Pageable pageable) {
        LocalDateTime startDateTime = condition.getStartDate() != null
                ? condition.getStartDate().atStartOfDay()
                : null;
        LocalDateTime endDateTime = condition.getEndDate() != null
                ? condition.getEndDate().atTime(LocalTime.MAX)
                : null;

        return orderRepository.findByCondition(
                condition.getKeyword(),
                condition.getStatus(),
                startDateTime,
                endDateTime,
                pageable
        ).map(AdminOrderResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminOrderResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        return AdminOrderResponse.from(order);
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        order.updateStatus(status);

        if (status == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                inventoryService.increase(item.getProductId(), item.getOptionId(),
                        item.getQty(), InventoryType.CANCEL, order.getId());
            }
        }
    }

    public void updateShipping(Long orderId, AdminShippingUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        order.updateStatus(OrderStatus.SHIPPED);

        OrderShipping shipping = orderShippingRepository.findByOrderId(orderId)
                .orElseGet(() -> OrderShipping.builder().orderId(orderId).build());

        shipping.update(request.getCourier(), request.getTrackingNumber());
        orderShippingRepository.save(shipping);

        emailService.sendShippingNotification(order);
    }

    public void bulkUpdateShipping(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            // skip header row (row 0), process from row 1
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String orderNumber = getCellStringValue(row, 0);
                String courier = getCellStringValue(row, 1);
                String trackingNumber = getCellStringValue(row, 2);

                if (orderNumber == null || orderNumber.isBlank()) continue;

                orderRepository.findByOrderNumber(orderNumber).ifPresent(order -> {
                    order.updateStatus(OrderStatus.SHIPPED);

                    OrderShipping shipping = orderShippingRepository.findByOrderId(order.getId())
                            .orElseGet(() -> OrderShipping.builder().orderId(order.getId()).build());

                    shipping.update(courier, trackingNumber);
                    orderShippingRepository.save(shipping);

                    emailService.sendShippingNotification(order);
                });
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    public void processRefund(Long orderId, AdminRefundRequest request) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.REFUND_REQUESTED) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        List<Long> itemIds = request.getOrderItemIds();

        for (OrderItem item : order.getItems()) {
            if (itemIds.contains(item.getId())) {
                item.updateStatus(OrderItemStatus.RETURNED);
                inventoryService.increase(item.getProductId(), item.getOptionId(),
                        item.getQty(), InventoryType.CANCEL, order.getId());
            }
        }

        boolean allReturned = order.getItems().stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.RETURNED);

        if (allReturned) {
            order.updateStatus(OrderStatus.REFUNDED);
            emailService.sendRefundNotification(order);
        }
    }

    private String getCellStringValue(Row row, int cellIndex) {
        var cell = row.getCell(cellIndex);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }
}
