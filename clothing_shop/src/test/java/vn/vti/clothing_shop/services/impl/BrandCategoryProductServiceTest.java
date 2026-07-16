package vn.vti.clothing_shop.services.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateRequest;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.BrandMapper;
import vn.vti.clothing_shop.mappers.CategoryMapper;
import vn.vti.clothing_shop.mappers.ProductMapper;
import vn.vti.clothing_shop.readmodels.ReadModelType;
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
	@Test
	void containsBrandCategoryAndProductServiceTests() {
		assertThat(List.of("brand", "category", "product")).hasSize(3);
	}

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
		void getBrandsReturnsMongoReadModelWhenAvailable() {
			Brand brand = new Brand(1L, "Nike", "Sport");
			when(readModelQueryService.findAll(ReadModelType.BRAND, Brand.class)).thenReturn(List.of(brand));

			assertThat(service.getBrands()).containsExactly(brand);
		}

		@Test
		void createBrandSavesAndReturnsEntity() throws WrapperException {
			BrandCreateRequest request = new BrandCreateRequest("Nike", "Sport");
			Brand brand = new Brand();
			Brand saved = new Brand();
			saved.setId(1L);

			when(brandRepository.existsByDeletedAtIsNullAndName("Nike")).thenReturn(false);
			when(brandMapper.createRequestToEntity(request)).thenReturn(brand);
			when(brandRepository.save(brand)).thenReturn(saved);

			Brand result = service.createBrand(request);

			assertThat(result).isSameAs(saved);
			verify(brandRepository).save(brand);
		}

		@Test
		void updateBrandSavesMappedEntityAndSyncsProducts() throws WrapperException {
			BrandUpdateRequest request = new BrandUpdateRequest("Nike Updated", "Sport", 2L);
			Brand existing = new Brand(1L, "Nike", "Sport");
			Brand mapped = new Brand(1L, "Nike Updated", "Sport");

			when(brandRepository.findById(1L)).thenReturn(Optional.of(existing));
			when(brandRepository.existsByDeletedAtIsNullAndIdNotAndName(1L, "Nike Updated")).thenReturn(false);
			when(brandMapper.updateRequestToEntity(request, existing)).thenReturn(mapped);
			when(brandRepository.save(mapped)).thenReturn(mapped);

			assertThat(service.updateBrand(request, 1L)).isSameAs(mapped);
			verify(readModelSyncService).syncAfterCommit(ReadModelType.BRAND, 1L);
			verify(readModelSyncService).syncAllProductsAfterCommit();
		}

		@Test
		void updateBrandWrapsDuplicateName() {
			BrandUpdateRequest request = new BrandUpdateRequest("Nike", "Sport", 2L);
			when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
			when(brandRepository.existsByDeletedAtIsNullAndIdNotAndName(1L, "Nike")).thenReturn(true);

			assertThatThrownBy(() -> service.updateBrand(request, 1L))
					.isInstanceOf(WrapperException.class);
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

		@Test
		void findBrandByIdFallsBackToRepository() throws WrapperException {
			Brand brand = new Brand(1L, "Nike", "Sport");
			when(readModelQueryService.findById(ReadModelType.BRAND, 1L, Brand.class)).thenReturn(Optional.empty());
			when(brandRepository.findByDeletedAtIsNullAndId(1L)).thenReturn(Optional.of(brand));

			assertThat(service.findBrandById(1L)).isSameAs(brand);
		}

		@Test
		void countBrandDelegatesToRepository() {
			when(brandRepository.countByDeletedAtIsNull()).thenReturn(3L);

			assertThat(service.countBrand()).isEqualTo(3L);
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
		void getAllCategoriesReturnsMongoReadModelWhenAvailable() {
			Category category = new Category(1L, "Shirt", "Top");
			when(readModelQueryService.findAll(ReadModelType.CATEGORY, Category.class)).thenReturn(List.of(category));

			assertThat(service.getAllCategories()).containsExactly(category);
		}

		@Test
		void addCategorySavesWhenNameIsUnique() throws WrapperException {
			CategoryCreateRequest request = new CategoryCreateRequest("Shirt", "Top");
			Category category = new Category();
			Category saved = new Category();
			saved.setId(1L);

			when(categoryRepository.existsByDeletedAtIsNullAndName("Shirt")).thenReturn(false);
			when(categoryMapper.createRequestToEntity(request)).thenReturn(category);
			when(categoryRepository.save(category)).thenReturn(saved);

			Category result = service.addCategory(request);

			assertThat(result).isSameAs(saved);
			verify(categoryRepository).save(category);
		}

		@Test
		void updateCategorySavesMappedEntityAndSyncsProducts() throws WrapperException {
			CategoryUpdateRequest request = new CategoryUpdateRequest("Shirt Updated", "Top", 2L);
			Category existing = new Category(1L, "Shirt", "Top");
			Category mapped = new Category(1L, "Shirt Updated", "Top");

			when(categoryRepository.existsByDeletedAtIsNullAndNameAndIdNot("Shirt Updated", 1L)).thenReturn(false);
			when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
			when(categoryMapper.updateRequestToEntity(request, existing)).thenReturn(mapped);
			when(categoryRepository.save(mapped)).thenReturn(mapped);

			assertThat(service.updateCategory(request, 1L)).isSameAs(mapped);
			verify(readModelSyncService).syncAfterCommit(ReadModelType.CATEGORY, 1L);
			verify(readModelSyncService).syncAllProductsAfterCommit();
		}

		@Test
		void updateCategoryWrapsDuplicateName() {
			CategoryUpdateRequest request = new CategoryUpdateRequest("Shirt", "Top", 2L);
			when(categoryRepository.existsByDeletedAtIsNullAndNameAndIdNot("Shirt", 1L)).thenReturn(true);

			assertThatThrownBy(() -> service.updateCategory(request, 1L))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void deleteCategorySoftDeletesExistingCategory() throws WrapperException {
			Category category = new Category();
			when(categoryRepository.findByDeletedAtIsNullAndId(1L)).thenReturn(Optional.of(category));

			service.deleteCategory(1L);

			assertThat(category.getDeletedAt()).isNotNull();
		}

		@Test
		void getCategoryByIdReturnsMongoReadModel() throws WrapperException {
			Category category = new Category(1L, "Shirt", "Top");
			when(readModelQueryService.findById(ReadModelType.CATEGORY, 1L, Category.class)).thenReturn(Optional.of(category));

			assertThat(service.getCategoryById(1L)).isSameAs(category);
		}

		@Test
		void countCategoryDelegatesToRepository() {
			when(categoryRepository.count()).thenReturn(5L);

			assertThat(service.countCategory()).isEqualTo(5L);
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
			when(readModelQueryService.findAll(ReadModelType.PRODUCT, Product.class)).thenReturn(List.of());
			when(productRepository.findByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(product));

			assertThat(service.getAllProducts()).containsExactly(product);
		}

		@Test
		void getAllProductsReturnsMongoReadModelWhenAvailable() {
			Product product = new Product(1L, "T-Shirt", "Cotton", new Category(), new Brand());
			when(readModelQueryService.findAll(ReadModelType.PRODUCT, Product.class)).thenReturn(List.of(product));

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
		void updateProductSavesMappedEntityWhenReferencesExist() throws WrapperException {
			ProductUpdateRequest request = new ProductUpdateRequest("T-Shirt", "Cotton", 1L, 2L, 3L);
			Product existing = new Product();
			Product mapped = new Product();
			mapped.setId(10L);
			Brand brand = new Brand();
			Category category = new Category();

			when(productRepository.findById(10L)).thenReturn(Optional.of(existing));
			when(brandRepository.findById(2L)).thenReturn(Optional.of(brand));
			when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
			when(productMapper.updateRequestToEntity(request, category, brand, existing)).thenReturn(mapped);
			when(productRepository.save(mapped)).thenReturn(mapped);

			service.updateProduct(request, 10L);

			verify(productRepository).save(mapped);
			verify(readModelSyncService).syncAfterCommit(ReadModelType.PRODUCT, 10L);
		}

		@Test
		void deleteProductSoftDeletesExistingProduct() throws WrapperException {
			Product product = new Product();
			when(productRepository.findById(10L)).thenReturn(Optional.of(product));

			service.deleteProduct(10L);

			assertThat(product.getDeletedAt()).isNotNull();
			verify(productRepository).save(product);
			verify(readModelSyncService).removeAfterCommit(ReadModelType.PRODUCT, 10L);
		}

		@Test
		void getProductByIdReturnsMongoReadModel() throws WrapperException {
			Product product = new Product();
			when(readModelQueryService.findById(ReadModelType.PRODUCT, 10L, Product.class)).thenReturn(Optional.of(product));

			assertThat(service.getProductById(10L)).isSameAs(product);
		}

		@Test
		void getProductByIdWrapsMissingProduct() {
			when(readModelQueryService.findById(ReadModelType.PRODUCT, 10L, Product.class)).thenReturn(Optional.empty());
			when(productRepository.findById(10L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.getProductById(10L))
					.isInstanceOf(WrapperException.class);
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
