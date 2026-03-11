package com.shop.domain.product.repository;

import com.shop.domain.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.sortOrder")
    List<Category> findRootCategories();

    List<Category> findByParentIdAndIsActiveTrue(Long parentId);
}
