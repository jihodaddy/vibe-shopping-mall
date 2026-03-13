package com.shop.domain.admin.dto;

import com.shop.domain.admin.entity.BannerPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBannerCreateRequest {

    @NotBlank(message = "배너 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "이미지 URL은 필수입니다.")
    private String imageUrl;

    private String linkUrl;

    @NotNull(message = "배너 위치는 필수입니다.")
    private BannerPosition position;

    private int sortOrder;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
