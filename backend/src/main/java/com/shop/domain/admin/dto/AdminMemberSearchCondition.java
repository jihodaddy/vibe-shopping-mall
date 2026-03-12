package com.shop.domain.admin.dto;

import com.shop.domain.member.entity.MemberGrade;
import com.shop.domain.member.entity.MemberStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMemberSearchCondition {

    private String keyword;
    private MemberGrade grade;
    private MemberStatus status;
}
