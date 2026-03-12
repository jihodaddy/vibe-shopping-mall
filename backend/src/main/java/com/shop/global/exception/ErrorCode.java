package com.shop.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 회원
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 어드민
    ADMIN_NOT_FOUND("어드민 계정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    // 인증
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 상품
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK("재고가 부족합니다.", HttpStatus.CONFLICT),

    // 주문/결제
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_PAYMENT("이미 결제가 진행 중입니다.", HttpStatus.CONFLICT),
    PAYMENT_AMOUNT_MISMATCH("결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_FAILED("결제 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 쿠폰
    COUPON_NOT_FOUND("쿠폰을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COUPON_EXPIRED("만료된 쿠폰입니다.", HttpStatus.BAD_REQUEST),
    COUPON_ALREADY_USED("이미 사용된 쿠폰입니다.", HttpStatus.CONFLICT),

    // 장바구니
    CART_ITEM_NOT_FOUND("장바구니 아이템을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 파일 업로드
    S3_UPLOAD_FAILED("파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;
}
