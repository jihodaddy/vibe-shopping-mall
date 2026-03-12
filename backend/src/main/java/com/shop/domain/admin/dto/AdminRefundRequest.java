package com.shop.domain.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminRefundRequest {

    @NotNull
    private List<Long> orderItemIds;
}
