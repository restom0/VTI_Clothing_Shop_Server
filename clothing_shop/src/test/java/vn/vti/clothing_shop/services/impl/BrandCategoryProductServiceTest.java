package vn.vti.clothing_shop.services.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.BrandMapper;
import vn.vti.clothing_shop.mappers.CategoryMapper;
import vn.vti.clothing_shop.mappers.ProductMapper;
import vn.vti.clothing_shop.repositories.BrandRepository;
import vn.vti.clothing_shop.repositories.CategoryRepository;
import vn.vti.clothing_shop.repositories.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BrandCategoryProductServiceTest {

	@ExtendWith(MockitoExtension.class)
	@Nested
	class BrandServiceTest {
		@Mock
		BrandRepository brandRepository;

		@Mock
		BrandMapper brandMapper;

		@Mock
		MongoReadModelQueryService readModelQueryService;

		@Mock
		PostgresToMongoReadModelSyncService readModelSyncService;

		@InjectMocks
		BrandServiceImpl service;

		@Test
		void createBrandSavesAndReturnsEntity() throws WrapperException {
			BrandCreateRequest request = new BrandCreateRequest("Nike", "Sport");
			Brand brand = new Brand();
			Brand saved = new Brand();

			when(brandRepository.existsByDeletedAtIsNullAndName("Nike")).thenReturn(false);
			when(brandMapper.createRequestToEntity(request)).thenReturn(brand);
			when(brandRepository.save(brand)).thenReturn(saved);

			Brand result = service.createBrand(request);

			assertThat(result).isSameAs(saved);
			verify(brandRepository).save(brand);
		}

		@Test
		void createBrandWrapsDuplicateName() {
			BrandCreateRequest request = new BrandCreateRequest("Nike", "Sport");
			when(brandRepository.existsByDeletedAtIsNullAndName("Nike")).thenReturn(true);

			assertThatThrownBy(() -> service.createBrand(request))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void deleteBrandSoftDeletesBrand() throws WrapperException {
			Brand brand = new Brand();
			when(brandRepository.findByDeletedAtIsNullAndId(1L)).thenReturn(Optional.of(brand));

			service.deleteBrand(1L);

			assertThat(brand.getDeletedAt()).isNotNull();
			verify(brandRepository).save(brand);
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class CategoryServiceTest {
		@Mock
		CategoryRepository categoryRepository;

		@Mock
		CategoryMapper categoryMapper;

		@Mock
		MongoReadModelQueryService readModelQueryService;

		@Mock
		PostgresToMongoReadModelSyncService readModelSyncService;

		@InjectMocks
		CategoryServiceImpl service;

		@Test
		void addCategorySavesWhenNameIsUnique() throws WrapperException {
			CategoryCreateRequest request = new CategoryCreateRequest("Shirt", "Top");
			Category category = new Category();
			Category saved = new Category();

			when(categoryRepository.existsByDeletedAtIsNullAndName("Shirt")).thenReturn(false);
			when(categoryMapper.createRequestToEntity(request)).thenReturn(category);
			when(categoryRepository.save(category)).thenReturn(saved);

			Category result = service.addCategory(request);

			assertThat(result).isSameAs(saved);
			verify(categoryRepository).save(category);
		}

		@Test
		void deleteCategorySoftDeletesExistingCategory() throws WrapperException {
			Category category = new Category();
			when(categoryRepository.findByDeletedAtIsNullAndId(1L)).thenReturn(Optional.of(category));

			service.deleteCategory(1L);

			assertThat(category.getDeletedAt()).isNotNull();
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class ProductServiceTest {
		@Mock
		ProductRepository productRepository;

		@Mock
		BrandRepository brandRepository;

		@Mock
		CategoryRepository categoryRepository;

		@Mock
		ProductMapper productMapper;

		@Mock
		MongoReadModelQueryService readModelQueryService;

		@Mock
		PostgresToMongoReadModelSyncService readModelSyncService;

		@InjectMocks
		ProductServiceImpl service;

		@Test
		void getAllProductsReturnsRepositoryResults() {
			Product product = new Product();
			when(productRepository.findByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(product));

			assertThat(service.getAllProducts()).containsExactly(product);
		}

		@Test
		void addProductSavesMappedEntityWhenReferencesExist() throws WrapperException {
			ProductCreateRequest request = new ProductCreateRequest("T-Shirt", "Cotton", 1L, 2L);
			Brand brand = new Brand();
			Category category = new Category();
			Product product = new Product();
			product.setId(1L);

			when(brandRepository.findById(2L)).thenReturn(Optional.of(brand));
			when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
			when(productMapper.createRequestToEntity(request, category, brand)).thenReturn(product);
			when(productRepository.save(product)).thenReturn(product);

			service.addProduct(request);

			verify(productRepository).save(product);
		}

		@Test
		void addProductWrapsMissingBrand() {
			ProductCreateRequest request = new ProductCreateRequest("T-Shirt", "Cotton", 1L, 2L);
			when(brandRepository.findById(2L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.addProduct(request))
					.isInstanceOf(WrapperException.class);
		}
	}
}
