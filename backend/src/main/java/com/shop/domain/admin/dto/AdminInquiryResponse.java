package com.shop.domain.admin.dto;

import com.shop.domain.cs.entity.Inquiry;
import com.shop.domain.cs.entity.InquiryStatus;
import com.shop.domain.cs.entity.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInquiryResponse {

    private Long id;
    private String memberName;
    private String memberEmail;
    private Long productId;
    private Long orderId;
    private InquiryType type;
    private String title;
    private String content;
    private InquiryStatus status;
    private boolean isSecret;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AdminInquiryAnswerResponse> answers;

    public static AdminInquiryResponse from(Inquiry inquiry) {
        return AdminInquiryResponse.builder()
            .id(inquiry.getId())
            .memberName(inquiry.getMember().getName())
            .memberEmail(inquiry.getMember().getEmail())
            .productId(inquiry.getProductId())
            .orderId(inquiry.getOrderId())
            .type(inquiry.getType())
            .title(inquiry.getTitle())
            .content(inquiry.getContent())
            .status(inquiry.getStatus())
            .isSecret(inquiry.isSecret())
            .createdAt(inquiry.getCreatedAt())
            .updatedAt(inquiry.getUpdatedAt())
            .build();
    }

    public static AdminInquiryResponse from(Inquiry inquiry, List<AdminInquiryAnswerResponse> answers) {
        return AdminInquiryResponse.builder()
            .id(inquiry.getId())
            .memberName(inquiry.getMember().getName())
            .memberEmail(inquiry.getMember().getEmail())
            .productId(inquiry.getProductId())
            .orderId(inquiry.getOrderId())
            .type(inquiry.getType())
            .title(inquiry.getTitle())
            .content(inquiry.getContent())
            .status(inquiry.getStatus())
            .isSecret(inquiry.isSecret())
            .createdAt(inquiry.getCreatedAt())
            .updatedAt(inquiry.getUpdatedAt())
            .answers(answers)
            .build();
    }
}
