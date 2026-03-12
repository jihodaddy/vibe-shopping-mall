package com.shop.domain.admin.service;

import com.shop.domain.admin.dto.*;
import com.shop.domain.inventory.entity.InventoryType;
import com.shop.domain.inventory.service.InventoryService;
import com.shop.domain.product.entity.Category;
import com.shop.domain.product.entity.Product;
import com.shop.domain.product.entity.ProductStatus;
import com.shop.domain.product.repository.CategoryRepository;
import com.shop.domain.product.repository.ProductRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private AdminProductService adminProductService;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
            .name("테스트 카테고리")
            .depth((byte) 1)
            .build();

        testProduct = Product.builder()
            .category(testCategory)
            .name("테스트 상품")
            .price(10000)
            .discountRate(10)
            .stockQty(100)
            .description("테스트 설명")
            .build();
    }

    @Test
    void 상품_등록_성공() {
        // given
        AdminProductCreateRequest request = new AdminProductCreateRequest(
            1L, "새 상품", "설명", 20000, 5, 50, null);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(testCategory));
        given(productRepository.save(any(Product.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminProductService.createProduct(request);

        // then
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void 상품_등록_이미지_포함_성공() {
        // given
        Map<String, Boolean> imageUrls = Map.of(
            "https://s3.example.com/product1.jpg", true,
            "https://s3.example.com/product2.jpg", false
        );
        AdminProductCreateRequest request = new AdminProductCreateRequest(
            1L, "이미지 상품", "설명", 30000, 0, 10, imageUrls);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(testCategory));
        given(productRepository.save(any(Product.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminProductService.createProduct(request);

        // then
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void 상품_등록_없는_카테고리_실패() {
        // given
        AdminProductCreateRequest request = new AdminProductCreateRequest(
            999L, "상품명", "설명", 10000, 0, 100, null);

        given(categoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProductService.createProduct(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);

        verify(productRepository, never()).save(any());
    }

    @Test
    void 상품_수정_성공() {
        // given
        AdminProductUpdateRequest request = new AdminProductUpdateRequest(
            "수정된 상품명", 15000, "수정된 설명", 20, 200, ProductStatus.ON_SALE);

        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));

        // when
        adminProductService.updateProduct(1L, request);

        // then
        verify(productRepository).findById(1L);
        assertThat(testProduct.getName()).isEqualTo("수정된 상품명");
        assertThat(testProduct.getPrice()).isEqualTo(15000);
        assertThat(testProduct.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    void 상품_수정_없는_상품_실패() {
        // given
        AdminProductUpdateRequest request = new AdminProductUpdateRequest(
            "수정된 상품명", 15000, "설명", 0, 100, ProductStatus.ON_SALE);

        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProductService.updateProduct(999L, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void 상품_삭제_성공_DELETED_상태로_변경() {
        // given
        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));

        // when
        adminProductService.deleteProduct(1L);

        // then
        verify(productRepository).findById(1L);
        assertThat(testProduct.getStatus()).isEqualTo(ProductStatus.DELETED);
    }

    @Test
    void 상품_삭제_없는_상품_실패() {
        // given
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProductService.deleteProduct(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void 상품_일괄_상태_변경() {
        // given
        List<Long> ids = List.of(1L, 2L, 3L);
        ProductStatus newStatus = ProductStatus.HIDDEN;

        // when
        adminProductService.bulkUpdateStatus(ids, newStatus);

        // then
        verify(productRepository).bulkUpdateStatus(ids, newStatus);
    }

    @Test
    void 상품_목록_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));
        given(productRepository.findByCondition(null, null, null, pageable))
            .willReturn(productPage);

        // when
        Page<AdminProductResponse> result = adminProductService.getProductList(pageable, null, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void 상품_상세_조회_성공() {
        // given
        given(productRepository.findByIdWithImages(1L))
            .willReturn(Optional.of(testProduct));

        // when
        AdminProductResponse response = adminProductService.getProductDetail(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("테스트 상품");
    }

    @Test
    void 상품_상세_조회_없는_상품_실패() {
        // given
        given(productRepository.findByIdWithImages(999L))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProductService.getProductDetail(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void 재고_증가_조정_성공() {
        // given
        AdminStockAdjustRequest request = new AdminStockAdjustRequest(null, 50);

        // when
        adminProductService.adjustStock(1L, request);

        // then
        verify(inventoryService).increase(1L, null, 50, InventoryType.ADMIN_ADJUST, null);
    }

    @Test
    void 재고_감소_조정_성공() {
        // given
        AdminStockAdjustRequest request = new AdminStockAdjustRequest(null, -30);

        // when
        adminProductService.adjustStock(1L, request);

        // then
        verify(inventoryService).decrease(1L, null, 30, InventoryType.ADMIN_ADJUST, null);
    }

    @Test
    void 카테고리_생성_성공() {
        // given
        AdminCategoryCreateRequest request = new AdminCategoryCreateRequest(null, "새 카테고리", 0);

        given(categoryRepository.save(any(Category.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminProductService.createCategory(request);

        // then
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void 서브_카테고리_생성_성공() {
        // given
        AdminCategoryCreateRequest request = new AdminCategoryCreateRequest(1L, "서브 카테고리", 0);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(testCategory));
        given(categoryRepository.save(any(Category.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adminProductService.createCategory(request);

        // then
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void 카테고리_삭제_비활성화() {
        // given
        given(categoryRepository.findById(1L)).willReturn(Optional.of(testCategory));

        // when
        adminProductService.deleteCategory(1L);

        // then
        assertThat(testCategory.isActive()).isFalse();
    }

    @Test
    void 카테고리_삭제_없는_카테고리_실패() {
        // given
        given(categoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProductService.deleteCategory(999L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
    }
}
