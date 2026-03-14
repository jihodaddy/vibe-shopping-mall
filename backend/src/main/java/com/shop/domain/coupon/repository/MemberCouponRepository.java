package com.shop.domain.coupon.repository;

import com.shop.domain.coupon.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);

    long countByCouponId(Long couponId);

    @Query("SELECT mc.coupon.id, COUNT(mc) FROM MemberCoupon mc WHERE mc.coupon.id IN :couponIds GROUP BY mc.coupon.id")
    List<Object[]> countByCouponIdIn(@Param("couponIds") List<Long> couponIds);
}
