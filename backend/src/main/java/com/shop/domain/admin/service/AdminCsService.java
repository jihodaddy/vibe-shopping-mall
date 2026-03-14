package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.admin.entity.AdminUser;
import com.shop.domain.admin.repository.AdminUserRepository;
import com.shop.domain.cs.entity.*;
import com.shop.domain.cs.repository.InquiryAnswerRepository;
import com.shop.domain.cs.repository.InquiryRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCsService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final AdminUserRepository adminUserRepository;

    @Transactional(readOnly = true)
    public Page<AdminInquiryResponse> getInquiryList(InquiryStatus status, InquiryType type,
                                                      String keyword, Pageable pageable) {
        return inquiryRepository.findByCondition(status, type, keyword, pageable)
            .map(AdminInquiryResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminInquiryResponse getInquiryDetail(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));

        List<InquiryAnswer> answers = inquiryAnswerRepository.findByInquiryIdOrderByCreatedAtAsc(id);
        List<AdminInquiryAnswerResponse> answerResponses = answers.stream()
            .map(AdminInquiryAnswerResponse::from)
            .toList();

        return AdminInquiryResponse.from(inquiry, answerResponses);
    }

    public void answerInquiry(Long inquiryId, Long adminId, AdminInquiryAnswerRequest request) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));

        if (inquiry.getStatus() == InquiryStatus.CLOSED) {
            throw new BusinessException(ErrorCode.INQUIRY_ALREADY_CLOSED);
        }

        AdminUser admin = adminUserRepository.findById(adminId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        InquiryAnswer answer = InquiryAnswer.builder()
            .inquiry(inquiry)
            .admin(admin)
            .content(request.getContent())
            .build();

        inquiryAnswerRepository.save(answer);
        inquiry.markAnswered();
    }

    public void closeInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));

        if (inquiry.getStatus() == InquiryStatus.CLOSED) {
            throw new BusinessException(ErrorCode.INQUIRY_ALREADY_CLOSED);
        }

        inquiry.close();
    }
}
