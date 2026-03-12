package com.shop.domain.order.repository;

import com.shop.domain.order.entity.OrderShipping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderShippingRepository extends JpaRepository<OrderShipping, Long> {

    Optional<OrderShipping> findByOrderId(Long orderId);
}
