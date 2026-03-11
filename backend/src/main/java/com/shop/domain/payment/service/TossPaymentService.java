package com.shop.domain.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@SuppressWarnings("unchecked")

@Service
@Slf4j
public class TossPaymentService {

    private static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String TOSS_CANCEL_URL  = "https://api.tosspayments.com/v1/payments/{paymentKey}/cancel";

    @Value("${payment.toss.secret-key}")
    private String secretKey;

    public Map<String, Object> confirm(String paymentKey, String orderId, int amount) {
        String encodedKey = Base64.getEncoder()
            .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        try {
            return WebClient.create()
                .post().uri(TOSS_CONFIRM_URL)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        } catch (WebClientResponseException e) {
            log.error("Toss payment confirm failed: {}", e.getResponseBodyAsString());
            throw new RuntimeException("토스페이먼츠 결제 승인 실패: " + e.getMessage());
        }
    }

    public Map<String, Object> cancel(String paymentKey, String cancelReason) {
        String encodedKey = Base64.getEncoder()
            .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        try {
            return WebClient.create()
                .post().uri(TOSS_CANCEL_URL, paymentKey)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("cancelReason", cancelReason))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        } catch (WebClientResponseException e) {
            log.error("Toss payment cancel failed: {}", e.getResponseBodyAsString());
            throw new RuntimeException("토스페이먼츠 결제 취소 실패: " + e.getMessage());
        }
    }
}
