package com.shop.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchKeywordResponse {

    private final List<KeywordItem> items;

    @Getter
    @Builder
    public static class KeywordItem {
        private final int rank;
        private final String keyword;
        private final long searchCount;
    }
}
