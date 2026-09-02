package vn.vti.clothing_shop.services.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.vti.clothing_shop.constants.ClothGender;
import vn.vti.clothing_shop.constants.Filter;
import vn.vti.clothing_shop.constants.InputSaleFilter;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Color;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.InputSale;
import vn.vti.clothing_shop.entities.Material;
import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.entities.Size;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.ColorMapper;
import vn.vti.clothing_shop.mappers.ImportedProductMapper;
import vn.vti.clothing_shop.mappers.InputSaleMapper;
import vn.vti.clothing_shop.mappers.MaterialMapper;
import vn.vti.clothing_shop.mappers.OnSaleProductMapper;
import vn.vti.clothing_shop.mappers.SizeMapper;
import vn.vti.clothing_shop.repositories.ColorRepository;
import vn.vti.clothing_shop.repositories.ImportedProductRepository;
import vn.vti.clothing_shop.repositories.InputSaleRepository;
import vn.vti.clothing_shop.repositories.MaterialRepository;
import vn.vti.clothing_shop.repositories.OnSaleProductRepository;
import vn.vti.clothing_shop.repositories.ProductRepository;
import vn.vti.clothing_shop.repositories.SizeRepository;
import vn.vti.clothing_shop.utils.TimeUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventorySaleServiceTest {
	@Test
	void containsInventoryAndSaleServiceTests() {
		assertThat(List.of("importedProduct", "inputSale", "onSaleProduct")).hasSize(3);
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class ImportedProductServiceTest {
		@Mock
		ImportedProductRepository importedProductRepository;

		@Mock
		ProductRepository productRepository;

		@Mock
		ColorRepository colorRepository;

		@Mock
		SizeRepository sizeRepository;

		@Mock
		MaterialRepository materialRepository;

		@Mock
		ImportedProductMapper importedProductMapper;

		@Mock
		ColorMapper colorMapper;

		@Mock
		SizeMapper sizeMapper;

		@Mock
		MaterialMapper materialMapper;

		@InjectMocks
		ImportedProductServiceImpl service;

		@Test
		void addImportedProductReusesExistingOptionsAndSavesMappedEntity() throws WrapperException {
			Category category = new Category();
			Product product = new Product();
			product.setCategory(category);
			Color color = new Color();
			Size size = new Size();
			Material material = new Material();
			ImportedProduct importedProduct = new ImportedProduct();
			ImportedProductCreateRequest request = createImportedProductRequest();

			when(productRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(product));
			when(colorRepository.findByDeletedAtIsNullAndCode("#FFFFFF")).thenReturn(Optional.of(color));
			when(sizeRepository.findByDeletedAtIsNullAndNameAndHeightAndWeight("M", "170", "65"))
					.thenReturn(Optional.of(size));
			when(materialRepository.findByDeletedAtIsNullAndName("Cotton")).thenReturn(Optional.of(material));
			when(importedProductMapper.createRequestToEntity(request, color, size, material, product))
					.thenReturn(importedProduct);

			service.addImportedProduct(request);

			verify(importedProductRepository).save(importedProduct);
		}

		@Test
		void addImportedProductCreatesMissingOptionsAndSavesMappedEntity() throws WrapperException {
			Category category = new Category();
			Product product = new Product();
			product.setCategory(category);
			Color color = new Color();
			Size size = new Size();
			Material material = new Material();
			ImportedProduct importedProduct = new ImportedProduct();
			ImportedProductCreateRequest request = createImportedProductRequest();

			when(productRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(product));
			when(colorRepository.findByDeletedAtIsNullAndCode("#FFFFFF")).thenReturn(Optional.empty());
			when(colorMapper.createRequestToEntity(request, category)).thenReturn(color);
			when(colorRepository.save(color)).thenReturn(color);
			when(sizeRepository.findByDeletedAtIsNullAndNameAndHeightAndWeight("M", "170", "65"))
					.thenReturn(Optional.empty());
			when(sizeMapper.createRequestToEntity(request, category)).thenReturn(size);
			when(sizeRepository.save(size)).thenReturn(size);
			when(materialRepository.findByDeletedAtIsNullAndName("Cotton")).thenReturn(Optional.empty());
			when(materialMapper.createRequestToEntity(request, category)).thenReturn(material);
			when(materialRepository.save(material)).thenReturn(material);
			when(importedProductMapper.createRequestToEntity(request, color, size, material, product))
					.thenReturn(importedProduct);

			service.addImportedProduct(request);

			verify(colorRepository).save(color);
			verify(sizeRepository).save(size);
			verify(materialRepository).save(material);
			verify(importedProductRepository).save(importedProduct);
		}

		private ImportedProductCreateRequest createImportedProductRequest() {
			return new ImportedProductCreateRequest(
					1L,
					"#FFFFFF",
					"White",
					"M",
					"170",
					"65",
					"Cotton",
					ClothGender.UNISEX,
					100000,
					"image",
					"slider1",
					"slider2",
					"slider3",
					"slider4",
					"public",
					"public1",
					"public2",
					"public3",
					"public4",
					20
			);
		}

		@Test
		void findImportedProductByIdWrapsMissingProduct() {
			when(importedProductRepository.findById(99L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.findImportedProductById(99L))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void findImportedProductByIdReturnsRepositoryEntity() throws WrapperException {
			ImportedProduct importedProduct = new ImportedProduct();
			when(importedProductRepository.findById(99L)).thenReturn(Optional.of(importedProduct));

			assertThat(service.findImportedProductById(99L)).isSameAs(importedProduct);
		}

		@Test
		void getImportedProductByFilterReturnsProductResults() {
			ImportedProduct product = new ImportedProduct();
			when(importedProductRepository.findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(1L, 0))
					.thenReturn(List.of(product));

			assertThat(service.getImportedProductByFilter(Filter.PRODUCT, 1L)).containsExactly(product);
		}

		@Test
		void getImportedProductByFilterCoversAllRepositoryBranches() {
			ImportedProduct importedProduct = new ImportedProduct();
			when(importedProductRepository.findAll()).thenReturn(List.of(importedProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndProduct_Category_IdAndStockGreaterThan(1L, 0))
					.thenReturn(List.of(importedProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndProduct_Brand_IdAndStockGreaterThan(1L, 0))
					.thenReturn(List.of(importedProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndColor_IdAndStockGreaterThan(1L, 0))
					.thenReturn(List.of(importedProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndSize_IdAndStockGreaterThan(1L, 0))
					.thenReturn(List.of(importedProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndMaterial_IdAndStockGreaterThan(1L, 0))
					.thenReturn(List.of(importedProduct));

			assertThat(service.getImportedProductByFilter(Filter.ALL, 1L)).containsExactly(importedProduct);
			assertThat(service.getImportedProductByFilter(Filter.CATEGORY, 1L)).containsExactly(importedProduct);
			assertThat(service.getImportedProductByFilter(Filter.BRAND, 1L)).containsExactly(importedProduct);
			assertThat(service.getImportedProductByFilter(Filter.COLOR, 1L)).containsExactly(importedProduct);
			assertThat(service.getImportedProductByFilter(Filter.SIZE, 1L)).containsExactly(importedProduct);
			assertThat(service.getImportedProductByFilter(Filter.MATERIAL, 1L)).containsExactly(importedProduct);
		}

		@Test
		void deleteImportedProductDeletesExistingEntity() throws WrapperException {
			ImportedProduct importedProduct = new ImportedProduct();
			when(importedProductRepository.findById(5L)).thenReturn(Optional.of(importedProduct));

			service.deleteImportedProduct(5L);

			verify(importedProductRepository).delete(importedProduct);
		}

		@Test
		void updateImportedProductSavesMappedEntity() throws WrapperException {
			ImportedProductUpdateRequest request = createImportedProductUpdateRequest();
			Category category = new Category();
			Product product = new Product();
			product.setCategory(category);
			ImportedProduct importedProduct = new ImportedProduct();
			Color color = new Color();
			Size size = new Size();
			Material material = new Material();
			Color mappedColor = new Color();
			Size mappedSize = new Size();
			Material mappedMaterial = new Material();
			ImportedProduct mappedImportedProduct = new ImportedProduct();

			when(importedProductRepository.findById(5L)).thenReturn(Optional.of(importedProduct));
			when(productRepository.findById(1L)).thenReturn(Optional.of(product));
			when(colorRepository.findById(2L)).thenReturn(Optional.of(color));
			when(sizeRepository.findById(3L)).thenReturn(Optional.of(size));
			when(materialRepository.findById(4L)).thenReturn(Optional.of(material));
			when(colorMapper.updateRequestToEntity(request, category, color)).thenReturn(mappedColor);
			when(colorRepository.save(mappedColor)).thenReturn(mappedColor);
			when(sizeMapper.updateRequestToEntity(request, category, size)).thenReturn(mappedSize);
			when(sizeRepository.save(mappedSize)).thenReturn(mappedSize);
			when(materialMapper.updateRequestToEntity(request, category, material)).thenReturn(mappedMaterial);
			when(materialRepository.save(mappedMaterial)).thenReturn(mappedMaterial);
			when(importedProductMapper.updateRequestToEntity(request, mappedColor, mappedSize, mappedMaterial, product,
			                                                importedProduct)).thenReturn(mappedImportedProduct);

			service.updateImportedProduct(5L, request);

			verify(importedProductRepository).save(mappedImportedProduct);
		}

		private ImportedProductUpdateRequest createImportedProductUpdateRequest() {
			return new ImportedProductUpdateRequest(
					1L,
					2L,
					3L,
					4L,
					"#FFFFFF",
					"White",
					"M",
					"170",
					"65",
					"Cotton",
					ClothGender.UNISEX,
					100000,
					20,
					"image",
					"slider1",
					"slider2",
					"slider3",
					"slider4",
					"public",
					"public1",
					"public2",
					"public3",
					"public4",
					1L
			);
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class InputSaleServiceTest {
		@Mock
		InputSaleRepository inputSaleRepository;

		@Mock
		OnSaleProductRepository onSaleProductRepository;

		@Mock
		ImportedProductRepository importedProductRepository;

		@Mock
		InputSaleMapper inputSaleMapper;

		@Mock
		OnSaleProductMapper onSaleProductMapper;

		@InjectMocks
		InputSaleServiceImpl service;

		@Test
		void createInputSaleCreatesOnSaleProductsWhenDateRangeDoesNotOverlap() {
			LocalDate startDate = LocalDate.now();
			LocalDate endDate = startDate.plusDays(7);
			InputSaleCreateRequest request = new InputSaleCreateRequest(
					InputSaleFilter.ALL,
					0L,
					120F,
					10F,
					startDate,
					endDate
			);
			InputSale inputSale = new InputSale();
			inputSale.setStartDate(startDate);
			inputSale.setEndDate(endDate);
			ImportedProduct importedProduct = new ImportedProduct();
			importedProduct.setId(1L);
			OnSaleProduct onSaleProduct = new OnSaleProduct();

			when(inputSaleMapper.createRequestEntity(request)).thenReturn(inputSale);
			when(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThan(anyInt()))
					.thenReturn(List.of(importedProduct));
			when(onSaleProductRepository.findByProductIdAndAvailableDateAndNotNullEndDate(1L, startDate, endDate))
					.thenReturn(Optional.empty());
			when(onSaleProductMapper.importProductAndInputSaleToOnSaleProduct(importedProduct, inputSale))
					.thenReturn(onSaleProduct);

			service.createInputSale(request);

			verify(inputSaleRepository).save(inputSale);
			verify(onSaleProductRepository).save(onSaleProduct);
		}

		@Test
		void createInputSaleSkipsWhenFilteredProductsAreEmpty() {
			LocalDate startDate = LocalDate.now();
			InputSaleCreateRequest request = new InputSaleCreateRequest(
					InputSaleFilter.PRODUCT,
					9L,
					120F,
					10F,
					startDate,
					startDate.plusDays(7)
			);
			InputSale inputSale = new InputSale();

			when(inputSaleMapper.createRequestEntity(request)).thenReturn(inputSale);
			when(importedProductRepository.findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(9L, 0))
					.thenReturn(List.of());

			service.createInputSale(request);

			verify(inputSaleRepository).save(inputSale);
			verify(onSaleProductMapper, never()).importProductAndInputSaleToOnSaleProduct(
					org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any()
			);
		}

		@Test
		void createInputSaleClosesOlderOpenSaleInsteadOfCreatingDuplicate() {
			LocalDate startDate = LocalDate.now().plusDays(2);
			InputSaleCreateRequest request = new InputSaleCreateRequest(
					InputSaleFilter.ALL,
					0L,
					120F,
					10F,
					startDate,
					null
			);
			InputSale inputSale = new InputSale();
			inputSale.setStartDate(startDate);
			ImportedProduct importedProduct = new ImportedProduct();
			importedProduct.setId(1L);
			InputSale olderSale = new InputSale();
			olderSale.setStartDate(startDate.minusDays(5));
			OnSaleProduct existingOnSaleProduct = new OnSaleProduct();
			existingOnSaleProduct.setInputSale(olderSale);

			when(inputSaleMapper.createRequestEntity(request)).thenReturn(inputSale);
			when(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThan(anyInt()))
					.thenReturn(List.of(importedProduct));
			when(onSaleProductRepository.findByProductIdAndAvailableDateAndNullEndDate(1L, startDate))
					.thenReturn(Optional.of(existingOnSaleProduct));

			service.createInputSale(request);

			assertThat(olderSale.getEndDate()).isNotNull();
			verify(onSaleProductRepository).save(existingOnSaleProduct);
			verify(onSaleProductMapper, never()).importProductAndInputSaleToOnSaleProduct(
					org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any()
			);
		}

		@Test
		void createInputSaleSkipsWhenOpenSaleStartsSameDay() {
			LocalDate startDate = LocalDate.now().plusDays(2);
			InputSaleCreateRequest request = new InputSaleCreateRequest(
					InputSaleFilter.ALL,
					0L,
					120F,
					10F,
					startDate,
					null
			);
			InputSale inputSale = new InputSale();
			inputSale.setStartDate(startDate);
			ImportedProduct importedProduct = new ImportedProduct();
			importedProduct.setId(1L);
			InputSale sameDaySale = new InputSale();
			sameDaySale.setStartDate(startDate);
			OnSaleProduct existingOnSaleProduct = new OnSaleProduct();
			existingOnSaleProduct.setInputSale(sameDaySale);

			when(inputSaleMapper.createRequestEntity(request)).thenReturn(inputSale);
			when(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThan(anyInt()))
					.thenReturn(List.of(importedProduct));
			when(onSaleProductRepository.findByProductIdAndAvailableDateAndNullEndDate(1L, startDate))
					.thenReturn(Optional.of(existingOnSaleProduct));

			service.createInputSale(request);

			assertThat(sameDaySale.getEndDate()).isNull();
			verify(onSaleProductMapper, never()).importProductAndInputSaleToOnSaleProduct(
					org.mockito.ArgumentMatchers.any(),
					org.mockito.ArgumentMatchers.any()
			);
		}

		@Test
		void createInputSaleCreatesOpenEndedSaleForProductThatIsNotOnSaleYet() {
			LocalDate startDate = LocalDate.now().plusDays(2);
			InputSaleCreateRequest request = new InputSaleCreateRequest(
					InputSaleFilter.ALL,
					0L,
					120F,
					10F,
					startDate,
					null
			);
			InputSale inputSale = new InputSale();
			inputSale.setStartDate(startDate);
			ImportedProduct importedProduct = new ImportedProduct();
			importedProduct.setId(1L);
			OnSaleProduct mapped = new OnSaleProduct();

			when(inputSaleMapper.createRequestEntity(request)).thenReturn(inputSale);
			when(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThan(anyInt()))
					.thenReturn(List.of(importedProduct));
			when(onSaleProductRepository.findByProductIdAndAvailableDateAndNullEndDate(1L, startDate))
					.thenReturn(Optional.empty());
			when(onSaleProductMapper.importProductAndInputSaleToOnSaleProduct(importedProduct, inputSale))
					.thenReturn(mapped);

			service.createInputSale(request);

			verify(onSaleProductRepository).save(mapped);
		}

		@Test
		void getInputSaleByIdWrapsMissingSale() {
			when(inputSaleRepository.findById(1L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.getInputSaleById(1L))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void updateInputSaleRepricesEligibleActiveProducts() throws WrapperException {
			LocalDate startDate = TimeUtils.today();
			InputSale inputSale = new InputSale();
			inputSale.setId(1L);
			inputSale.setStartDate(startDate);
			inputSale.setEndDate(startDate.plusDays(7));
			inputSale.setSalePercentage(150F);
			inputSale.setDiscount(20F);
			InputSaleUpdateRequest request = new InputSaleUpdateRequest(
					125F,
					15F,
					startDate,
					startDate.plusDays(10),
					1L
			);
			InputSale mapped = new InputSale();
			mapped.setId(1L);
			mapped.setStartDate(startDate);
			mapped.setEndDate(startDate.plusDays(10));
			mapped.setSalePercentage(125F);
			mapped.setDiscount(15F);
			ImportedProduct importedProduct = new ImportedProduct();
			importedProduct.setId(5L);
			OnSaleProduct onSaleProduct = new OnSaleProduct();
			onSaleProduct.setProduct(importedProduct);
			onSaleProduct.setSalePrice(100L);
			onSaleProduct.setInputSale(new InputSale());

			when(inputSaleRepository.findById(1L)).thenReturn(Optional.of(inputSale));
			when(onSaleProductRepository.findByInputSale_IdAndInputSale_StartDateLessThanEqualAndDeletedAtIsNullAndInputSale_DeletedAtIsNullOrderByIdDesc(
					1L,
					TimeUtils.today()
			)).thenReturn(List.of(onSaleProduct));
			when(onSaleProductRepository.findByProductIdAndAvailableDateAndNotNullEndDate(
					5L,
					startDate,
					startDate.plusDays(10)
			)).thenReturn(Optional.empty());
			when(inputSaleMapper.updateRequestToEntity(request, inputSale)).thenReturn(mapped);

			service.updateInputSale(request, 1L);

			// Repricing uses the requested percentage/discount, not the pre-update ones.
			assertThat(onSaleProduct.getSalePrice()).isEqualTo(125L);
			assertThat(onSaleProduct.getInputSale().getDiscount()).isEqualTo(15F);
			verify(onSaleProductRepository).save(onSaleProduct);
			verify(inputSaleRepository).save(mapped);
		}

		@Test
		void deleteInputSaleMarksSaleAndProductsDeleted() {
			InputSale inputSale = new InputSale();
			OnSaleProduct onSaleProduct = new OnSaleProduct();
			when(inputSaleRepository.findById(1L)).thenReturn(Optional.of(inputSale));
			when(onSaleProductRepository.findByInputSale_IdAndInputSale_StartDateLessThanEqualAndDeletedAtIsNullAndInputSale_DeletedAtIsNullOrderByIdDesc(
					1L,
					TimeUtils.today()
			)).thenReturn(List.of(onSaleProduct));

			service.deleteInputSale(1L);

			assertThat(inputSale.getDeletedAt()).isNotNull();
			assertThat(onSaleProduct.getDeletedAt()).isNotNull();
			verify(onSaleProductRepository).saveAll(List.of(onSaleProduct));
			verify(inputSaleRepository).save(inputSale);
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class OnSaleProductServiceTest {
		@Mock
		OnSaleProductRepository onSaleProductRepository;

		@Mock
		OnSaleProductMapper onSaleProductMapper;

		@InjectMocks
		OnSaleProductServiceImpl service;

		@Test
		void getAllOnSaleProductsReturnsRepositoryResults() {
			OnSaleProduct entity = new OnSaleProduct();
			when(onSaleProductRepository.findDistinctByDeletedAtIsNull()).thenReturn(List.of(entity));

			assertThat(service.getAllOnSaleProducts()).containsExactly(entity);
		}

		@Test
		void getOnSaleProductByIdReturnsRepositoryResults() {
			OnSaleProduct entity = new OnSaleProduct();
			when(onSaleProductRepository.findAllByProductId(7L)).thenReturn(List.of(entity));

			assertThat(service.getOnSaleProductById(7L)).containsExactly(entity);
		}

		@Test
		void onSaleProductComputesSalePriceFromImportPriceAndSalePercentage() {
			ImportedProduct importedProduct = new ImportedProduct();
			importedProduct.setImportPrice(100000);
			InputSale inputSale = new InputSale();
			inputSale.setSalePercentage(125F);
			OnSaleProduct onSaleProduct = new OnSaleProduct(1L, null, importedProduct, inputSale);

			assertThat(onSaleProduct.getSalePrice()).isEqualTo(125000L);

			onSaleProduct.setSalePrice(90000L);
			assertThat(onSaleProduct.getSalePrice()).isEqualTo(90000L);
			assertThat(new OnSaleProduct().getSalePrice()).isZero();
		}
	}
}
