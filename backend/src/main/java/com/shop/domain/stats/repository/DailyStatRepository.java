package com.shop.domain.stats.repository;

import com.shop.domain.stats.entity.DailyStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatRepository extends JpaRepository<DailyStat, Long> {

    List<DailyStat> findByStatDateBetweenOrderByStatDateAsc(LocalDate from, LocalDate to);
}
