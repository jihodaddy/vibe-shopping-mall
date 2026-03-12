package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.member.entity.Member;
import com.shop.domain.member.entity.MemberGrade;
import com.shop.domain.member.entity.MemberStatus;
import com.shop.domain.member.repository.MemberRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminMemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<AdminMemberResponse> getMemberList(AdminMemberSearchCondition condition, Pageable pageable) {
        return memberRepository.findByCondition(
                condition.getKeyword(),
                condition.getGrade(),
                condition.getStatus(),
                pageable
        ).map(AdminMemberResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminMemberResponse getMemberDetail(Long memberId) {
        Member member = findById(memberId);
        return AdminMemberResponse.from(member);
    }

    public void changeGrade(Long memberId, MemberGrade grade) {
        Member member = findById(memberId);
        member.changeGrade(grade);
    }

    public void addPoint(Long memberId, AdminMemberPointRequest request) {
        Member member = findById(memberId);
        member.addPoint(request.getAmount());
        // TODO: 포인트 이력 엔티티 구현 시 reason 저장 예정
        log.info("어드민 포인트 지급: memberId={}, amount={}, reason={}",
            memberId, request.getAmount(), request.getReason());
    }

    public void updateStatus(Long memberId, MemberStatus status) {
        Member member = findById(memberId);
        member.updateStatus(status);
    }

    private Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
