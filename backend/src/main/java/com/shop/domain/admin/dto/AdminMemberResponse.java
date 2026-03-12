package com.shop.domain.admin.dto;

import com.shop.domain.member.entity.Member;
import com.shop.domain.member.entity.MemberGrade;
import com.shop.domain.member.entity.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminMemberResponse {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private MemberGrade grade;
    private int point;
    private MemberStatus status;
    private LocalDateTime createdAt;

    public static AdminMemberResponse from(Member member) {
        return AdminMemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .grade(member.getGrade())
                .point(member.getPoint())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
