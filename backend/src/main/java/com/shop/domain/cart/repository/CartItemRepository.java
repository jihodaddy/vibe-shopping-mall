package com.shop.domain.cart.repository;

import com.shop.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductIdAndOptionId(Long cartId, Long productId, Long optionId);
}
