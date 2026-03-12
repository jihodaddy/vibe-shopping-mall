package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.AdminMemberPointRequest;
import com.shop.domain.admin.dto.AdminMemberResponse;
import com.shop.domain.admin.dto.AdminMemberSearchCondition;
import com.shop.domain.member.entity.Member;
import com.shop.domain.member.entity.MemberGrade;
import com.shop.domain.member.entity.MemberStatus;
import com.shop.domain.member.repository.MemberRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminMemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AdminMemberService adminMemberService;

    private Member testMember;

    @BeforeEach
    void setUp() throws Exception {
        testMember = Member.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .name("홍길동")
                .phone("010-1234-5678")
                .build();

        setId(testMember, 1L);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field field = getFieldFromHierarchy(entity.getClass(), "id");
        field.setAccessible(true);
        field.set(entity, id);
    }

    private Field getFieldFromHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getFieldFromHierarchy(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }

    @Test
    void 회원_목록_조회_성공() {
        // given
        AdminMemberSearchCondition condition = new AdminMemberSearchCondition();
        Pageable pageable = PageRequest.of(0, 20);
        Page<Member> memberPage = new PageImpl<>(List.of(testMember), pageable, 1);

        given(memberRepository.findByCondition(null, null, null, pageable))
                .willReturn(memberPage);

        // when
        Page<AdminMemberResponse> result = adminMemberService.getMemberList(condition, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("test@example.com");
        assertThat(result.getContent().get(0).getName()).isEqualTo("홍길동");
        verify(memberRepository).findByCondition(null, null, null, pageable);
    }

    @Test
    void 회원_상세_조회_성공() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));

        // when
        AdminMemberResponse result = adminMemberService.getMemberDetail(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getGrade()).isEqualTo(MemberGrade.BRONZE);
        assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    void 등급_변경_성공() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));

        // when
        adminMemberService.changeGrade(1L, MemberGrade.GOLD);

        // then
        assertThat(testMember.getGrade()).isEqualTo(MemberGrade.GOLD);
        verify(memberRepository).findById(1L);
    }

    @Test
    void 존재하지_않는_회원_등급변경_실패() {
        // given
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminMemberService.changeGrade(999L, MemberGrade.GOLD))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    void 포인트_지급_성공() throws Exception {
        // given
        AdminMemberPointRequest request = new AdminMemberPointRequest();
        Field amountField = AdminMemberPointRequest.class.getDeclaredField("amount");
        amountField.setAccessible(true);
        amountField.set(request, 1000);
        Field reasonField = AdminMemberPointRequest.class.getDeclaredField("reason");
        reasonField.setAccessible(true);
        reasonField.set(request, "이벤트 포인트 지급");

        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));

        // when
        adminMemberService.addPoint(1L, request);

        // then
        assertThat(testMember.getPoint()).isEqualTo(1000);
        verify(memberRepository).findById(1L);
    }

    @Test
    void 회원_정지_성공() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));

        // when
        adminMemberService.updateStatus(1L, MemberStatus.INACTIVE);

        // then
        assertThat(testMember.getStatus()).isEqualTo(MemberStatus.INACTIVE);
        verify(memberRepository).findById(1L);
    }
}
