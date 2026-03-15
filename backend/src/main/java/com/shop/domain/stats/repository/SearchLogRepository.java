package com.shop.domain.stats.repository;

import com.shop.domain.stats.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    @Query("SELECT s.keyword, COUNT(s) as cnt FROM SearchLog s " +
           "WHERE s.searchedAt BETWEEN :from AND :to " +
           "GROUP BY s.keyword ORDER BY cnt DESC")
    List<Object[]> findTopKeywords(@Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to);
}
