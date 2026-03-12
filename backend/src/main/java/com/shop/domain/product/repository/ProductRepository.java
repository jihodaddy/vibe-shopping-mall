package com.shop.domain.product.repository;

import com.shop.domain.product.entity.Product;
import com.shop.domain.product.entity.ProductStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.status = :status WHERE p.id IN :ids")
    void bulkUpdateStatus(@Param("ids") List<Long> ids, @Param("status") ProductStatus status);

    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR p.name LIKE %:keyword%) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Product> findByCondition(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("status") ProductStatus status,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
}
