package com.shop.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminInquiryAnswerRequest {

    @NotBlank(message = "답변 내용은 필수입니다.")
    private String content;
}
