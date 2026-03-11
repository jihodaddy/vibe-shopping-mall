package com.shop.global.exception;

import com.shop.global.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusinessException_반환값_검증() {
        BusinessException ex = new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getErrorCode()).isEqualTo("MEMBER_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo("회원을 찾을 수 없습니다.");
    }

    @Test
    void handleBusinessException_OUT_OF_STOCK_검증() {
        BusinessException ex = new BusinessException(ErrorCode.OUT_OF_STOCK);
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorCode()).isEqualTo("OUT_OF_STOCK");
    }
}
