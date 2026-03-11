package com.shop.domain.order.service;

import com.shop.domain.cart.entity.CartItem;
import com.shop.domain.cart.repository.CartRepository;
import com.shop.domain.inventory.entity.InventoryType;
import com.shop.domain.inventory.service.InventoryService;
import com.shop.domain.order.dto.OrderCreateRequest;
import com.shop.domain.order.dto.OrderCreateResponse;
import com.shop.domain.order.entity.Order;
import com.shop.domain.order.entity.OrderItem;
import com.shop.domain.order.entity.OrderStatus;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.payment.entity.Payment;
import com.shop.domain.payment.entity.PaymentMethod;
import com.shop.domain.payment.entity.PaymentStatus;
import com.shop.domain.payment.repository.PaymentRepository;
import com.shop.domain.product.entity.Product;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final PaymentRepository paymentRepository;

    public OrderCreateResponse createOrder(Long memberId, OrderCreateRequest request) {
        // 1. 장바구니 조회
        var cart = cartRepository.findByMemberIdWithItems(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
        List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // 2. 상품 금액 계산
        int totalPrice = cartItems.stream().mapToInt(item -> {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            return product.getDiscountPrice() * item.getQty();
        }).sum();

        int shippingFee = totalPrice >= 50000 ? 0 : 3000;
        int couponDiscount = 0;   // TODO: 쿠폰 적용
        int pointDiscount = Math.min(request.getUsePoint(), totalPrice);
        int finalPrice = totalPrice - couponDiscount - pointDiscount + shippingFee;

        // 3. 주문 생성
        String orderNumber = generateOrderNumber();
        Order order = Order.builder()
            .memberId(memberId)
            .orderNumber(orderNumber)
            .receiverName(request.getReceiverName())
            .receiverPhone(request.getReceiverPhone())
            .zipcode(request.getZipcode())
            .address(request.getAddress())
            .addressDetail(request.getAddressDetail())
            .deliveryMemo(request.getDeliveryMemo())
            .totalPrice(totalPrice)
            .couponDiscount(couponDiscount)
            .pointDiscount(pointDiscount)
            .shippingFee(shippingFee)
            .finalPrice(finalPrice)
            .build();
        orderRepository.save(order);

        // 4. 주문 아이템 생성 + 재고 임시 차감
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productId(product.getId())
                .optionId(cartItem.getOptionId())
                .productName(product.getName())
                .qty(cartItem.getQty())
                .price(product.getDiscountPrice())
                .build();
            order.addItem(orderItem);

            inventoryService.decrease(product.getId(), cartItem.getOptionId(),
                cartItem.getQty(), InventoryType.ORDER, order.getId());
        }

        // 5. 결제 레코드 생성 (READY)
        String idempotencyKey = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
            .orderId(order.getId())
            .idempotencyKey(idempotencyKey)
            .method(PaymentMethod.valueOf(request.getPaymentMethod()))
            .amount(finalPrice)
            .status(PaymentStatus.READY)
            .build();
        paymentRepository.save(payment);

        // 6. 장바구니 비우기
        cart.getItems().clear();

        return OrderCreateResponse.of(orderNumber, idempotencyKey, finalPrice);
    }

    public void cancelOrder(Long memberId, Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        order.updateStatus(OrderStatus.CANCELLED);

        // 재고 복원
        for (OrderItem item : order.getItems()) {
            inventoryService.increase(item.getProductId(), item.getOptionId(),
                item.getQty(), InventoryType.CANCEL, orderId);
        }
    }

    private String generateOrderNumber() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
