package com.shop.domain.admin.dto;

import com.shop.domain.member.entity.MemberStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMemberStatusUpdateRequest {

    @NotNull
    private MemberStatus status;
}
