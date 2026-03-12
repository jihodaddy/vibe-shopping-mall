package com.shop.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {

    private String presignedUrl;
    private String fileUrl;
}
