package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.admin.entity.AdminUser;
import com.shop.domain.admin.entity.AdminRole;
import com.shop.domain.admin.repository.AdminUserRepository;
import com.shop.domain.cs.entity.*;
import com.shop.domain.cs.repository.InquiryAnswerRepository;
import com.shop.domain.cs.repository.InquiryRepository;
import com.shop.domain.member.entity.Member;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCsServiceTest {

    @Mock
    private InquiryRepository inquiryRepository;

    @Mock
    private InquiryAnswerRepository inquiryAnswerRepository;

    @Mock
    private AdminUserRepository adminUserRepository;

    @InjectMocks
    private AdminCsService adminCsService;

    private Member testMember;
    private Inquiry testInquiry;
    private AdminUser testAdmin;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
            .email("test@test.com")
            .name("테스트 회원")
            .password("password")
            .phone("01012345678")
            .build();

        testInquiry = Inquiry.builder()
            .member(testMember)
            .type(InquiryType.PRODUCT)
            .title("상품 문의")
            .content("상품에 대해 궁금합니다.")
            .isSecret(false)
            .build();

        testAdmin = AdminUser.builder()
            .email("admin@shop.com")
            .name("관리자")
            .password("password")
            .role(AdminRole.ADMIN)
            .build();
    }

    @Test
    void 문의_목록_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Inquiry> inquiryPage = new PageImpl<>(List.of(testInquiry));
        given(inquiryRepository.findByCondition(null, null, null, pageable))
            .willReturn(inquiryPage);

        // when
        Page<AdminInquiryResponse> result = adminCsService.getInquiryList(null, null, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("상품 문의");
    }

    @Test
    void 문의_목록_상태필터_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Inquiry> inquiryPage = new PageImpl<>(List.of(testInquiry));
        given(inquiryRepository.findByCondition(InquiryStatus.PENDING, null, null, pageable))
            .willReturn(inquiryPage);

        // when
        Page<AdminInquiryResponse> result = adminCsService.getInquiryList(InquiryStatus.PENDING, null, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void 문의_상세_조회_성공() {
        // given
        given(inquiryRepository.findById(1L)).willReturn(Optional.of(testInquiry));
        given(inquiryAnswerRepository.findByInquiryIdOrderByCreatedAtAsc(1L)).willReturn(List.of());

        // when
        AdminInquiryResponse response = adminCsService.getInquiryDetail(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("상품 문의");
        assertThat(response.getAnswers()).isEmpty();
    }

    @Test
    void 문의_상세_조회_없는_문의_실패() {
        // given
        given(inquiryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCsService.getInquiryDetail(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INQUIRY_NOT_FOUND);
    }

    @Test
    void 문의_답변_성공() {
        // given
        AdminInquiryAnswerRequest request = new AdminInquiryAnswerRequest("답변 내용입니다.");

        given(inquiryRepository.findById(1L)).willReturn(Optional.of(testInquiry));
        given(adminUserRepository.findById(1L)).willReturn(Optional.of(testAdmin));
        given(inquiryAnswerRepository.save(any(InquiryAnswer.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminCsService.answerInquiry(1L, 1L, request);

        // then
        verify(inquiryAnswerRepository).save(any(InquiryAnswer.class));
        assertThat(testInquiry.getStatus()).isEqualTo(InquiryStatus.ANSWERED);
    }

    @Test
    void 문의_답변_없는_문의_실패() {
        // given
        AdminInquiryAnswerRequest request = new AdminInquiryAnswerRequest("답변 내용");
        given(inquiryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCsService.answerInquiry(999L, 1L, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INQUIRY_NOT_FOUND);
    }

    @Test
    void 문의_답변_종료된_문의_실패() {
        // given
        testInquiry.close();
        AdminInquiryAnswerRequest request = new AdminInquiryAnswerRequest("답변 내용");
        given(inquiryRepository.findById(1L)).willReturn(Optional.of(testInquiry));

        // when & then
        assertThatThrownBy(() -> adminCsService.answerInquiry(1L, 1L, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INQUIRY_ALREADY_CLOSED);
    }

    @Test
    void 문의_종료_성공() {
        // given
        given(inquiryRepository.findById(1L)).willReturn(Optional.of(testInquiry));

        // when
        adminCsService.closeInquiry(1L);

        // then
        assertThat(testInquiry.getStatus()).isEqualTo(InquiryStatus.CLOSED);
    }

    @Test
    void 문의_종료_없는_문의_실패() {
        // given
        given(inquiryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCsService.closeInquiry(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INQUIRY_NOT_FOUND);
    }

    @Test
    void 문의_종료_이미_종료된_문의_실패() {
        // given
        testInquiry.close();
        given(inquiryRepository.findById(1L)).willReturn(Optional.of(testInquiry));

        // when & then
        assertThatThrownBy(() -> adminCsService.closeInquiry(1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INQUIRY_ALREADY_CLOSED);
    }
}
