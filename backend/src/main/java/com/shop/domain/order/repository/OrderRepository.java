package com.shop.domain.order.repository;

import com.shop.domain.order.entity.Order;
import com.shop.domain.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    Page<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o WHERE " +
           "(:keyword IS NULL OR o.orderNumber LIKE %:keyword% OR o.receiverName LIKE %:keyword%) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR o.createdAt <= :endDate) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findByCondition(
            @Param("keyword") String keyword,
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
