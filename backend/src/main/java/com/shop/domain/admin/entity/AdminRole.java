package com.shop.domain.admin.entity;

public enum AdminRole {
    SUPER_ADMIN,
    ADMIN,
    // CS_AGENT: 현재 어드민 API에 대한 접근 권한이 없습니다. 향후 태스크에서 접근 규칙이 정의될 예정입니다.
    CS_AGENT
}
