package com.shop.domain.admin.dto;

import com.shop.domain.admin.entity.Banner;
import com.shop.domain.admin.entity.BannerPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBannerResponse {

    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private BannerPosition position;
    private int sortOrder;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private boolean active;

    public static AdminBannerResponse from(Banner banner) {
        return AdminBannerResponse.builder()
            .id(banner.getId())
            .title(banner.getTitle())
            .imageUrl(banner.getImageUrl())
            .linkUrl(banner.getLinkUrl())
            .position(banner.getPosition())
            .sortOrder(banner.getSortOrder())
            .startAt(banner.getStartAt())
            .endAt(banner.getEndAt())
            .active(banner.isActive())
            .build();
    }
}
