package com.shop.domain.cs.repository;

import com.shop.domain.cs.entity.Inquiry;
import com.shop.domain.cs.entity.InquiryStatus;
import com.shop.domain.cs.entity.InquiryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Query("SELECT i FROM Inquiry i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:type IS NULL OR i.type = :type) AND " +
           "(:keyword IS NULL OR i.title LIKE CONCAT('%', :keyword, '%') OR i.content LIKE CONCAT('%', :keyword, '%'))")
    Page<Inquiry> findByCondition(
        @Param("status") InquiryStatus status,
        @Param("type") InquiryType type,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}
