package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.content.entity.Banner;
import com.shop.domain.content.entity.BannerPosition;
import com.shop.domain.content.repository.BannerRepository;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminContentService {

    private final BannerRepository bannerRepository;

    public Long createBanner(AdminBannerCreateRequest request) {
        validateBannerDateRange(request.getStartAt(), request.getEndAt());

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
        validateBannerDateRange(request.getStartAt(), request.getEndAt());

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
        Map<Long, Banner> bannerMap = bannerRepository.findAllById(bannerIds).stream()
            .collect(Collectors.toMap(Banner::getId, Function.identity()));

        for (int i = 0; i < bannerIds.size(); i++) {
            Banner banner = bannerMap.get(bannerIds.get(i));
            if (banner == null) {
                throw new BusinessException(ErrorCode.BANNER_NOT_FOUND);
            }
            banner.updateSortOrder(i);
        }
    }

    private void validateBannerDateRange(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt != null && endAt != null && !endAt.isAfter(startAt)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }
    }
}
