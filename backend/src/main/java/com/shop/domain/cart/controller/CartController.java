package com.shop.domain.cart.controller;

import com.shop.domain.cart.dto.CartAddRequest;
import com.shop.domain.cart.dto.CartItemResponse;
import com.shop.domain.cart.service.CartService;
import com.shop.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.getMemberCart(memberId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addItem(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody CartAddRequest request) {
        if (memberId != null) {
            cartService.addMemberItem(memberId, request);
        } else {
            cartService.addGuestItem(request.getGuestKey(), request);
        }
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long cartItemId) {
        cartService.removeItem(memberId, cartItemId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/merge")
    public ResponseEntity<ApiResponse<Void>> mergeGuestCart(
            @AuthenticationPrincipal Long memberId,
            @RequestParam String guestKey) {
        cartService.mergeGuestCart(memberId, guestKey);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
