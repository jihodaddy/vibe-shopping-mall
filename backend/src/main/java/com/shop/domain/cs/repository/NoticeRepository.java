package com.shop.domain.cs.repository;

import com.shop.domain.cs.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n WHERE " +
           "(:isActive IS NULL OR n.isActive = :isActive) AND " +
           "(:keyword IS NULL OR n.title LIKE CONCAT('%', :keyword, '%'))" +
           "ORDER BY n.isPinned DESC, n.createdAt DESC")
    Page<Notice> findByCondition(
        @Param("isActive") Boolean isActive,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}
