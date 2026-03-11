package com.shop.domain.cart.service;

import com.shop.domain.cart.dto.CartAddRequest;
import com.shop.domain.cart.dto.CartItemResponse;
import com.shop.domain.cart.entity.Cart;
import com.shop.domain.cart.entity.CartItem;
import com.shop.domain.cart.repository.CartItemRepository;
import com.shop.domain.cart.repository.CartRepository;
import com.shop.domain.member.entity.Member;
import com.shop.domain.member.repository.MemberRepository;
import com.shop.domain.product.entity.Product;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private static final String GUEST_CART_PREFIX = "guest:cart:";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /** 비회원: Redis에 저장 */
    public void addGuestItem(String guestKey, CartAddRequest request) {
        String redisKey = GUEST_CART_PREFIX + guestKey;
        String field = request.getProductId() + ":" + request.getOptionId();
        Object existing = redisTemplate.opsForHash().get(redisKey, field);

        int qty = request.getQty();
        if (existing != null) {
            qty += (int) existing;
        }
        redisTemplate.opsForHash().put(redisKey, field, qty);
        redisTemplate.expire(redisKey, Duration.ofDays(7));
    }

    /** 회원: DB에 저장 */
    public void addMemberItem(Long memberId, CartAddRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Cart cart = cartRepository.findByMemberIdWithItems(memberId)
            .orElseGet(() -> cartRepository.save(Cart.builder().member(member).build()));

        Optional<CartItem> existing = cartItemRepository
            .findByCartIdAndProductIdAndOptionId(cart.getId(), request.getProductId(), request.getOptionId());

        if (existing.isPresent()) {
            existing.get().addQty(request.getQty());
        } else {
            cartItemRepository.save(CartItem.builder()
                .cart(cart)
                .productId(request.getProductId())
                .optionId(request.getOptionId())
                .qty(request.getQty())
                .build());
        }
    }

    /** 로그인 시 비회원 장바구니 → DB 병합 */
    public void mergeGuestCart(Long memberId, String guestKey) {
        String redisKey = GUEST_CART_PREFIX + guestKey;
        Map<Object, Object> guestItems = redisTemplate.opsForHash().entries(redisKey);

        if (guestItems.isEmpty()) return;

        for (Map.Entry<Object, Object> entry : guestItems.entrySet()) {
            String[] parts = entry.getKey().toString().split(":");
            Long productId = Long.parseLong(parts[0]);
            Long optionId = parts[1].equals("null") ? null : Long.parseLong(parts[1]);
            int qty = (int) entry.getValue();

            mergeItem(memberId, productId, optionId, qty);
        }
        redisTemplate.delete(redisKey);
    }

    private void mergeItem(Long memberId, Long productId, Long optionId, int qty) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Cart cart = cartRepository.findByMemberIdWithItems(memberId)
            .orElseGet(() -> cartRepository.save(Cart.builder().member(member).build()));

        cartItemRepository.findByCartIdAndProductIdAndOptionId(cart.getId(), productId, optionId)
            .ifPresentOrElse(
                item -> item.addQty(qty),
                () -> cartItemRepository.save(CartItem.builder()
                    .cart(cart).productId(productId).optionId(optionId).qty(qty).build())
            );
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getMemberCart(Long memberId) {
        Cart cart = cartRepository.findByMemberIdWithItems(memberId).orElse(null);
        if (cart == null) return Collections.emptyList();

        return cart.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null) return null;
            String thumbnail = product.getImages().stream()
                .filter(img -> img.isMain()).findFirst()
                .map(img -> img.getUrl()).orElse(null);
            return new CartItemResponse(
                item.getId(), item.getProductId(), product.getName(),
                item.getOptionId(), null, product.getDiscountPrice(),
                item.getQty(), thumbnail
            );
        }).filter(Objects::nonNull).toList();
    }

    public void removeItem(Long memberId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
        cartItemRepository.delete(item);
    }
}
