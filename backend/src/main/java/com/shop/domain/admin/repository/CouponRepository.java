package com.shop.domain.admin.repository;

import com.shop.domain.admin.entity.Coupon;
import com.shop.domain.admin.entity.CouponType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT c FROM Coupon c WHERE " +
           "(:keyword IS NULL OR c.name LIKE %:keyword% OR c.code LIKE %:keyword%) AND " +
           "(:type IS NULL OR c.type = :type) AND " +
           "(:isActive IS NULL OR c.isActive = :isActive)")
    Page<Coupon> findByCondition(
        @Param("keyword") String keyword,
        @Param("type") CouponType type,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
}
