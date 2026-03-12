package com.shop.domain.admin.dto;

import com.shop.domain.member.entity.MemberGrade;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMemberGradeUpdateRequest {

    @NotNull
    private MemberGrade grade;
}
