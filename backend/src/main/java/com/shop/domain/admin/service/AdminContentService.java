package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.admin.entity.Banner;
import com.shop.domain.admin.entity.BannerPosition;
import com.shop.domain.admin.repository.BannerRepository;
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
public class AdminContentService {

    private final BannerRepository bannerRepository;

    public Long createBanner(AdminBannerCreateRequest request) {
        Banner banner = Banner.builder()
            .title(request.getTitle())
            .imageUrl(request.getImageUrl())
            .linkUrl(request.getLinkUrl())
            .position(request.getPosition())
            .sortOrder(request.getSortOrder())
            .startAt(request.getStartAt())
            .endAt(request.getEndAt())
            .build();

        bannerRepository.save(banner);
        return banner.getId();
    }

    public void updateBanner(Long id, AdminBannerUpdateRequest request) {
        Banner banner = bannerRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.BANNER_NOT_FOUND));

        banner.update(
            request.getTitle(),
            request.getImageUrl(),
            request.getLinkUrl(),
            request.getPosition(),
            request.getSortOrder(),
            request.getStartAt(),
            request.getEndAt()
        );
    }

    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.BANNER_NOT_FOUND));
        banner.deactivate();
    }

    @Transactional(readOnly = true)
    public Page<AdminBannerResponse> getBannerList(Pageable pageable,
                                                    BannerPosition position, Boolean isActive) {
        return bannerRepository.findByCondition(position, isActive, pageable)
            .map(AdminBannerResponse::from);
    }

    @Transactional(readOnly = true)
    public AdminBannerResponse getBannerDetail(Long id) {
        Banner banner = bannerRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.BANNER_NOT_FOUND));
        return AdminBannerResponse.from(banner);
    }

    public void updateBannerSort(AdminBannerSortRequest request) {
        List<Long> bannerIds = request.getBannerIds();
        for (int i = 0; i < bannerIds.size(); i++) {
            Banner banner = bannerRepository.findById(bannerIds.get(i))
                .orElseThrow(() -> new BusinessException(ErrorCode.BANNER_NOT_FOUND));
            banner.updateSortOrder(i);
        }
    }
}
