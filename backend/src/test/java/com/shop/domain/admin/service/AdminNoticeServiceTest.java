package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.cs.entity.Notice;
import com.shop.domain.cs.repository.NoticeRepository;
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
class AdminNoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private AdminNoticeService adminNoticeService;

    private Notice testNotice;

    @BeforeEach
    void setUp() {
        testNotice = Notice.builder()
            .title("테스트 공지")
            .content("공지 내용입니다.")
            .isPinned(false)
            .build();
    }

    @Test
    void 공지사항_생성_성공() {
        // given
        AdminNoticeCreateRequest request = new AdminNoticeCreateRequest("새 공지", "공지 내용", true);
        given(noticeRepository.save(any(Notice.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminNoticeService.createNotice(request);

        // then
        verify(noticeRepository).save(any(Notice.class));
    }

    @Test
    void 공지사항_수정_성공() {
        // given
        AdminNoticeUpdateRequest request = new AdminNoticeUpdateRequest("수정된 공지", "수정된 내용", true);
        given(noticeRepository.findById(1L)).willReturn(Optional.of(testNotice));

        // when
        adminNoticeService.updateNotice(1L, request);

        // then
        assertThat(testNotice.getTitle()).isEqualTo("수정된 공지");
        assertThat(testNotice.getContent()).isEqualTo("수정된 내용");
        assertThat(testNotice.isPinned()).isTrue();
    }

    @Test
    void 공지사항_수정_없는_공지_실패() {
        // given
        AdminNoticeUpdateRequest request = new AdminNoticeUpdateRequest("수정", "내용", false);
        given(noticeRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminNoticeService.updateNotice(999L, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOTICE_NOT_FOUND);
    }

    @Test
    void 공지사항_삭제_성공() {
        // given
        given(noticeRepository.findById(1L)).willReturn(Optional.of(testNotice));

        // when
        adminNoticeService.deleteNotice(1L);

        // then
        assertThat(testNotice.isActive()).isFalse();
    }

    @Test
    void 공지사항_삭제_없는_공지_실패() {
        // given
        given(noticeRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminNoticeService.deleteNotice(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOTICE_NOT_FOUND);
    }

    @Test
    void 공지사항_목록_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Notice> noticePage = new PageImpl<>(List.of(testNotice));
        given(noticeRepository.findByCondition(null, null, pageable))
            .willReturn(noticePage);

        // when
        Page<AdminNoticeResponse> result = adminNoticeService.getNoticeList(pageable, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 공지");
    }

    @Test
    void 공지사항_상세_조회_성공() {
        // given
        given(noticeRepository.findById(1L)).willReturn(Optional.of(testNotice));

        // when
        AdminNoticeResponse response = adminNoticeService.getNoticeDetail(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("테스트 공지");
        assertThat(response.getViewCount()).isEqualTo(1); // incremented
    }

    @Test
    void 공지사항_상세_조회_없는_공지_실패() {
        // given
        given(noticeRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminNoticeService.getNoticeDetail(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOTICE_NOT_FOUND);
    }
}
