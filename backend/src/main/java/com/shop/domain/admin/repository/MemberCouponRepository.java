package com.shop.domain.admin.repository;

import com.shop.domain.admin.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);

    long countByCouponId(Long couponId);
}
