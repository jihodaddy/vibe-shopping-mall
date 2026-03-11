package com.shop.domain.inventory.repository;

import com.shop.domain.inventory.entity.InventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
}
