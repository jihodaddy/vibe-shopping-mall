package com.shop.domain.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMemberPointRequest {

    @NotNull
    @Min(1)
    private int amount;

    @NotBlank
    private String reason;
}
