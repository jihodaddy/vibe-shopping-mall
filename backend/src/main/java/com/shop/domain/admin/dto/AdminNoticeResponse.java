package com.shop.domain.admin.dto;

import com.shop.domain.cs.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNoticeResponse {

    private Long id;
    private String title;
    private String content;
    private boolean isPinned;
    private boolean isActive;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdminNoticeResponse from(Notice notice) {
        return AdminNoticeResponse.builder()
            .id(notice.getId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .isPinned(notice.isPinned())
            .isActive(notice.isActive())
            .viewCount(notice.getViewCount())
            .createdAt(notice.getCreatedAt())
            .updatedAt(notice.getUpdatedAt())
            .build();
    }
}
