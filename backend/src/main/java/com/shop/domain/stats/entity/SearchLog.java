package com.shop.domain.stats.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String keyword;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "result_count", nullable = false)
    private int resultCount;

    @Column(name = "searched_at", nullable = false)
    private LocalDateTime searchedAt;

    @Builder
    public SearchLog(String keyword, Long memberId, int resultCount, LocalDateTime searchedAt) {
        this.keyword = keyword;
        this.memberId = memberId;
        this.resultCount = resultCount;
        this.searchedAt = searchedAt != null ? searchedAt : LocalDateTime.now();
    }
}
