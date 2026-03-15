package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.cs.entity.Notice;
import com.shop.domain.cs.repository.NoticeRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminNoticeService {

    private final NoticeRepository noticeRepository;

    public Long createNotice(AdminNoticeCreateRequest request) {
        Notice notice = Notice.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .isPinned(request.isPinned())
            .build();

        noticeRepository.save(notice);
        return notice.getId();
    }

    public void updateNotice(Long id, AdminNoticeUpdateRequest request) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));

        notice.update(request.getTitle(), request.getContent(), request.isPinned());
    }

    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));

        notice.deactivate();
    }

    @Transactional(readOnly = true)
    public Page<AdminNoticeResponse> getNoticeList(Pageable pageable, String keyword, Boolean isActive) {
        return noticeRepository.findByCondition(isActive, keyword, pageable)
            .map(AdminNoticeResponse::from);
    }

    @Transactional
    public AdminNoticeResponse getNoticeDetail(Long id) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));

        notice.incrementViewCount();
        return AdminNoticeResponse.from(notice);
    }
}
