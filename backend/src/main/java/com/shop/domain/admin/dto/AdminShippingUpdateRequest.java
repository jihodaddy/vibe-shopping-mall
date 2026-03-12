package com.shop.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminShippingUpdateRequest {

    @NotBlank
    private String courier;

    @NotBlank
    private String trackingNumber;
}
