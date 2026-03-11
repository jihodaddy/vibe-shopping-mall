package com.shop.domain.inventory.service;

import com.shop.domain.inventory.entity.InventoryHistory;
import com.shop.domain.inventory.entity.InventoryType;
import com.shop.domain.inventory.repository.InventoryHistoryRepository;
import com.shop.domain.product.entity.Product;
import com.shop.domain.product.entity.ProductOption;
import com.shop.domain.product.repository.ProductOptionRepository;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;
    private final InventoryHistoryRepository historyRepository;

    public void decrease(Long productId, Long optionId, int qty,
                         InventoryType type, Long orderId) {
        if (optionId != null) {
            ProductOption option = optionRepository.findByIdWithLock(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            if (option.getStockQty() < qty) {
                throw new BusinessException(ErrorCode.OUT_OF_STOCK);
            }
            option.decreaseStock(qty);
            saveHistory(productId, optionId, -qty, option.getStockQty(), type, orderId);
        } else {
            Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            if (product.getStockQty() < qty) {
                throw new BusinessException(ErrorCode.OUT_OF_STOCK);
            }
            product.decreaseStock(qty);
            saveHistory(productId, null, -qty, product.getStockQty(), type, orderId);
        }
    }

    public void increase(Long productId, Long optionId, int qty,
                         InventoryType type, Long orderId) {
        if (optionId != null) {
            ProductOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            option.increaseStock(qty);
            saveHistory(productId, optionId, qty, option.getStockQty(), type, orderId);
        } else {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            product.increaseStock(qty);
            saveHistory(productId, null, qty, product.getStockQty(), type, orderId);
        }
    }

    private void saveHistory(Long productId, Long optionId, int delta, int balanceAfter,
                              InventoryType type, Long orderId) {
        historyRepository.save(InventoryHistory.builder()
            .productId(productId)
            .optionId(optionId)
            .type(type)
            .delta(delta)
            .balanceAfter(balanceAfter)
            .orderId(orderId)
            .build());
    }
}
