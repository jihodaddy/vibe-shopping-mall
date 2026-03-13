package com.shop.domain.admin.repository;

import com.shop.domain.admin.entity.Banner;
import com.shop.domain.admin.entity.BannerPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("SELECT b FROM Banner b WHERE " +
           "(:position IS NULL OR b.position = :position) AND " +
           "(:isActive IS NULL OR b.isActive = :isActive) " +
           "ORDER BY b.sortOrder ASC")
    Page<Banner> findByCondition(
        @Param("position") BannerPosition position,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );

    List<Banner> findByPositionAndIsActiveTrueOrderBySortOrderAsc(BannerPosition position);
}
