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
import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.outs.ImportedProductDTO;
import vn.vti.clothing_shop.dtos.outs.OnSaleProductDTO;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventorySaleServiceTest {

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
        void findImportedProductByIdWrapsMissingProduct() {
            when(importedProductRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findImportedProductById(99L))
                    .isInstanceOf(WrapperException.class);
        }

        @Test
        void getImportedProductByFilterMapsProductResults() {
            ImportedProduct product = new ImportedProduct();
            ImportedProductDTO dto = new ImportedProductDTO();
            when(importedProductRepository.findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(1L, 0))
                    .thenReturn(List.of(product));
            when(importedProductMapper.entityToDTO(product)).thenReturn(dto);

            assertThat(service.getImportedProductByFilter(Filter.PRODUCT, 1L)).containsExactly(dto);
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
        void getInputSaleByIdWrapsMissingSale() {
            when(inputSaleRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getInputSaleById(1L))
                    .isInstanceOf(WrapperException.class);
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
        void getAllOnSaleProductsDelegatesListMapping() {
            OnSaleProduct entity = new OnSaleProduct();
            OnSaleProductDTO dto = new OnSaleProductDTO();
            when(onSaleProductRepository.findDistinctByDeletedAtIsNull()).thenReturn(List.of(entity));
            when(onSaleProductMapper.entityToDTO(List.of(entity))).thenReturn(List.of(dto));

            assertThat(service.getAllOnSaleProducts()).containsExactly(dto);
        }
    }
}
