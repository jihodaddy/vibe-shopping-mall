package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.content.entity.Banner;
import com.shop.domain.content.entity.BannerPosition;
import com.shop.domain.content.repository.BannerRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminContentServiceTest {

    @Mock
    private BannerRepository bannerRepository;

    @InjectMocks
    private AdminContentService adminContentService;

    private Banner testBanner;

    @BeforeEach
    void setUp() {
        testBanner = Banner.builder()
            .title("테스트 배너")
            .imageUrl("https://s3.example.com/banner1.jpg")
            .linkUrl("https://shop.com/event/1")
            .position(BannerPosition.MAIN_TOP)
            .sortOrder(0)
            .startAt(LocalDateTime.now().minusDays(1))
            .endAt(LocalDateTime.now().plusDays(30))
            .build();
    }

    @Test
    void 배너_생성_성공() {
        // given
        AdminBannerCreateRequest request = new AdminBannerCreateRequest(
            "새 배너", "https://s3.example.com/new.jpg", "https://shop.com",
            BannerPosition.MAIN_TOP, 1, LocalDateTime.now(), LocalDateTime.now().plusDays(7)
        );

        given(bannerRepository.save(any(Banner.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminContentService.createBanner(request);

        // then
        verify(bannerRepository).save(any(Banner.class));
    }

    @Test
    void 배너_수정_성공() {
        // given
        AdminBannerUpdateRequest request = new AdminBannerUpdateRequest(
            "수정된 배너", "https://s3.example.com/updated.jpg", "https://shop.com/updated",
            BannerPosition.MAIN_MIDDLE, 2, LocalDateTime.now(), LocalDateTime.now().plusDays(14)
        );

        given(bannerRepository.findById(1L)).willReturn(Optional.of(testBanner));

        // when
        adminContentService.updateBanner(1L, request);

        // then
        assertThat(testBanner.getTitle()).isEqualTo("수정된 배너");
        assertThat(testBanner.getPosition()).isEqualTo(BannerPosition.MAIN_MIDDLE);
    }

    @Test
    void 배너_수정_없는_배너_실패() {
        // given
        AdminBannerUpdateRequest request = new AdminBannerUpdateRequest(
            "수정", "https://s3.example.com/img.jpg", null,
            BannerPosition.MAIN_TOP, 0, null, null
        );

        given(bannerRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminContentService.updateBanner(999L, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BANNER_NOT_FOUND);
    }

    @Test
    void 배너_삭제_비활성화() {
        // given
        given(bannerRepository.findById(1L)).willReturn(Optional.of(testBanner));

        // when
        adminContentService.deleteBanner(1L);

        // then
        assertThat(testBanner.isActive()).isFalse();
    }

    @Test
    void 배너_삭제_없는_배너_실패() {
        // given
        given(bannerRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminContentService.deleteBanner(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BANNER_NOT_FOUND);
    }

    @Test
    void 배너_목록_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Banner> bannerPage = new PageImpl<>(List.of(testBanner));
        given(bannerRepository.findByCondition(null, null, pageable))
            .willReturn(bannerPage);

        // when
        Page<AdminBannerResponse> result = adminContentService.getBannerList(pageable, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 배너");
    }

    @Test
    void 배너_상세_조회_성공() {
        // given
        given(bannerRepository.findById(1L)).willReturn(Optional.of(testBanner));

        // when
        AdminBannerResponse response = adminContentService.getBannerDetail(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("테스트 배너");
        assertThat(response.getPosition()).isEqualTo(BannerPosition.MAIN_TOP);
    }

    @Test
    void 배너_상세_조회_없는_배너_실패() {
        // given
        given(bannerRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminContentService.getBannerDetail(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BANNER_NOT_FOUND);
    }

    @Test
    void 배너_정렬순서_변경_성공() {
        // given
        Banner banner1 = Banner.builder()
            .title("배너1").imageUrl("url1").position(BannerPosition.MAIN_TOP).sortOrder(0).build();
        Banner banner2 = Banner.builder()
            .title("배너2").imageUrl("url2").position(BannerPosition.MAIN_TOP).sortOrder(1).build();

        // ID를 리플렉션으로 설정
        setField(banner1, "id", 1L);
        setField(banner2, "id", 2L);

        AdminBannerSortRequest request = new AdminBannerSortRequest(List.of(2L, 1L));

        given(bannerRepository.findAllById(List.of(2L, 1L))).willReturn(List.of(banner1, banner2));

        // when
        adminContentService.updateBannerSort(request);

        // then
        assertThat(banner2.getSortOrder()).isEqualTo(0);
        assertThat(banner1.getSortOrder()).isEqualTo(1);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
