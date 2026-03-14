package com.shop.domain.admin.dto;

import com.shop.domain.cs.entity.InquiryAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInquiryAnswerResponse {

    private Long id;
    private String adminName;
    private String content;
    private LocalDateTime createdAt;

    public static AdminInquiryAnswerResponse from(InquiryAnswer answer) {
        return AdminInquiryAnswerResponse.builder()
            .id(answer.getId())
            .adminName(answer.getAdmin().getName())
            .content(answer.getContent())
            .createdAt(answer.getCreatedAt())
            .build();
    }
}
