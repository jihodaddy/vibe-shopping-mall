package com.shop.domain.cs.repository;

import com.shop.domain.cs.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {

    List<InquiryAnswer> findByInquiryIdOrderByCreatedAtAsc(Long inquiryId);
}
