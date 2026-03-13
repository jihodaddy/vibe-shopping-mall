package com.shop.domain.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBannerSortRequest {

    @NotNull(message = "배너 ID 목록은 필수입니다.")
    private List<Long> bannerIds;
}
