package com.shop.global.service;

import com.shop.domain.order.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Async
    public void sendShippingNotification(Order order) {
        // TODO: AWS SES 구현 예정
        log.info("배송 알림 발송 (stub): orderId={}, orderNumber={}",
                order.getId(), order.getOrderNumber());
    }

    @Async
    public void sendRefundNotification(Order order) {
        log.info("환불 알림 발송 (stub): orderId={}, orderNumber={}",
                order.getId(), order.getOrderNumber());
    }
}
