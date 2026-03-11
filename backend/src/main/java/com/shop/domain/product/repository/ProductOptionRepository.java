package com.shop.domain.product.repository;

import com.shop.domain.product.entity.ProductOption;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT o FROM ProductOption o WHERE o.id = :id")
    Optional<ProductOption> findByIdWithLock(@Param("id") Long id);
}
