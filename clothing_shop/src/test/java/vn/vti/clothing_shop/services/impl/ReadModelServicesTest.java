package vn.vti.clothing_shop.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;
import vn.vti.clothing_shop.dtos.outs.VoucherDTO;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.mappers.BrandMapper;
import vn.vti.clothing_shop.mappers.CategoryMapper;
import vn.vti.clothing_shop.mappers.OrderMapper;
import vn.vti.clothing_shop.mappers.ProductMapper;
import vn.vti.clothing_shop.mappers.VoucherMapper;
import vn.vti.clothing_shop.readmodels.ReadModelDocument;
import vn.vti.clothing_shop.readmodels.ReadModelRepository;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.repositories.BrandRepository;
import vn.vti.clothing_shop.repositories.CategoryRepository;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.repositories.ProductRepository;
import vn.vti.clothing_shop.repositories.VoucherRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadModelServicesTest {
	@Mock
	private ReadModelRepository readModelRepository;
	@Mock
	private BrandRepository brandRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private VoucherRepository voucherRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private BrandMapper brandMapper;
	@Mock
	private CategoryMapper categoryMapper;
	@Mock
	private ProductMapper productMapper;
	@Mock
	private VoucherMapper voucherMapper;
	@Mock
	private OrderMapper orderMapper;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@AfterEach
	void clearTransactionSynchronization() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.clearSynchronization();
		}
	}

	@Test
	void mongoQueryServiceReadsAllLookupShapesAndHandlesFailures() {
		MongoReadModelQueryService service = new MongoReadModelQueryService(readModelRepository, objectMapper);
		ReadModelDocument document = document(ReadModelType.BRAND, 1L, null, Map.of(
				"id", 1L,
				"name", "Nike",
				"description", "Shoes"
		));

		when(readModelRepository.findByModelAndDeletedFalseOrderBySortValueDesc("BRAND")).thenReturn(List.of(document));
		when(readModelRepository.findByModelAndOwnerIdAndDeletedFalseOrderBySortValueDesc("BRAND", 9L))
				.thenReturn(List.of(document));
		when(readModelRepository.findByModelAndEntityIdAndDeletedFalse("BRAND", 1L)).thenReturn(Optional.of(document));
		when(readModelRepository.findByModelAndEntityIdAndOwnerIdAndDeletedFalse("BRAND", 1L, 9L))
				.thenReturn(Optional.of(document));
		when(readModelRepository.findByModelAndLookupKeyAndDeletedFalse("BRAND", "nike")).thenReturn(Optional.of(document));
		when(readModelRepository.findByModelAndDeletedFalseOrderBySortValueDesc("CATEGORY"))
				.thenThrow(new DataRetrievalFailureException("mongo down"));

		assertThat(service.findAll(ReadModelType.BRAND, BrandDTO.class)).extracting(BrandDTO::getName)
		                                                               .containsExactly("Nike");
		assertThat(service.findByOwner(ReadModelType.BRAND, 9L, BrandDTO.class)).extracting(BrandDTO::getId)
		                                                                        .containsExactly(1L);
		assertThat(service.findById(ReadModelType.BRAND, 1L, BrandDTO.class)).get().extracting(BrandDTO::getDescription)
		                                                                     .isEqualTo("Shoes");
		assertThat(service.findByIdAndOwner(ReadModelType.BRAND, 1L, 9L, BrandDTO.class)).isPresent();
		assertThat(service.findByLookupKey(ReadModelType.BRAND, "nike", BrandDTO.class)).isPresent();
		assertThat(service.findAll(ReadModelType.CATEGORY, CategoryDTO.class)).isEmpty();
	}

	@Test
	void syncServiceSavesEveryModelAndRemovesMissingOnes() {
		PostgresToMongoReadModelSyncService service = syncService();
		Brand brand = brand(1L);
		Category category = category(2L);
		Product product = product(3L);
		Voucher voucher = voucher(4L);
		Order order = order(5L, 9L);

		when(brandRepository.findByDeletedAtIsNullAndId(1L)).thenReturn(Optional.of(brand));
		when(brandMapper.entityToDTO(brand)).thenReturn(new BrandDTO(1L, "Nike", "Shoes"));
		when(categoryRepository.findByDeletedAtIsNullAndId(2L)).thenReturn(Optional.of(category));
		when(categoryMapper.entityToDTO(category)).thenReturn(new CategoryDTO(2L, "Sneakers", "Daily shoes"));
		when(productRepository.findByIdAndDeletedAtIsNull(3L)).thenReturn(Optional.of(product));
		when(productMapper.entityToDTO(product)).thenReturn(new ProductDTO(3L, "Air", "Light", null, null));
		when(voucherRepository.findByDeletedAtIsNullAndId(4L)).thenReturn(Optional.of(voucher));
		when(voucherMapper.entityToDTO(voucher)).thenReturn(new VoucherDTO(4L, 10, 15F, "SALE15", 1L, 2L));
		when(orderRepository.findByDeletedAtIsNullAndId(5L)).thenReturn(Optional.of(order));
		when(orderMapper.entityToDTO(order)).thenReturn(new OrderDTO(5L, "Street", "0912345678", "Ada", false,
		                                                              100L, 555L, null, null, null, List.of()));
		when(productRepository.findByIdAndDeletedAtIsNull(30L)).thenReturn(Optional.empty());

		service.sync(ReadModelType.BRAND, 1L);
		service.sync(ReadModelType.CATEGORY, 2L);
		service.sync(ReadModelType.PRODUCT, 3L);
		service.sync(ReadModelType.VOUCHER, 4L);
		service.sync(ReadModelType.ORDER, 5L);
		service.sync(ReadModelType.PRODUCT, 30L);
		service.sync(null, null);
		service.remove(ReadModelType.BRAND, null);

		ArgumentCaptor<ReadModelDocument> captor = ArgumentCaptor.forClass(ReadModelDocument.class);
		verify(readModelRepository, org.mockito.Mockito.times(5)).save(captor.capture());
		assertThat(captor.getAllValues()).extracting(ReadModelDocument::getId)
		                                 .containsExactly("BRAND:1", "CATEGORY:2", "PRODUCT:3", "VOUCHER:4",
		                                                  "ORDER:5");
		assertThat(captor.getAllValues().get(3).getLookupKey()).isEqualTo("SALE15");
		assertThat(captor.getAllValues().get(4).getOwnerId()).isEqualTo(9L);
		assertThat(captor.getAllValues().get(0).getPayload()).containsEntry("name", "Nike");
		verify(readModelRepository).deleteById("PRODUCT:30");
		verify(readModelRepository, never()).deleteById("BRAND:null");
	}

	@Test
	void syncServiceRunsAfterCommitAndSwallowsDataAccessFailures() {
		PostgresToMongoReadModelSyncService service = syncService();
		TransactionSynchronizationManager.initSynchronization();

		service.removeAfterCommit(ReadModelType.BRAND, 1L);
		verifyNoInteractions(readModelRepository);
		for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
			synchronization.afterCommit();
		}
		verify(readModelRepository).deleteById("BRAND:1");

		doThrow(new DataRetrievalFailureException("mongo down")).when(readModelRepository).deleteById("BRAND:2");
		service.remove(ReadModelType.BRAND, 2L);
		verify(readModelRepository).deleteById("BRAND:2");
	}

	@Test
	void bootstrapRunnerSyncsKnownEntitiesAndSkipsWhenRepositoryFails() {
		PostgresToMongoReadModelSyncService syncService = org.mockito.Mockito.mock(
				PostgresToMongoReadModelSyncService.class);
		ReadModelBootstrapRunner runner = new ReadModelBootstrapRunner(syncService, brandRepository, categoryRepository,
		                                                               productRepository, voucherRepository,
		                                                               orderRepository);
		when(brandRepository.findByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(brand(1L)));
		when(categoryRepository.findAllByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(category(2L)));
		when(productRepository.findByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(product(3L)));
		when(voucherRepository.findByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(voucher(4L)));
		when(orderRepository.findByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(order(5L, 9L)));

		runner.run(null);

		verify(syncService).sync(ReadModelType.BRAND, 1L);
		verify(syncService).sync(ReadModelType.CATEGORY, 2L);
		verify(syncService).sync(ReadModelType.PRODUCT, 3L);
		verify(syncService).sync(ReadModelType.VOUCHER, 4L);
		verify(syncService).sync(ReadModelType.ORDER, 5L);

		when(brandRepository.findByDeletedAtIsNullOrderByIdDesc()).thenThrow(new IllegalStateException("db down"));
		runner.run(null);
	}

	private PostgresToMongoReadModelSyncService syncService() {
		return new PostgresToMongoReadModelSyncService(readModelRepository, brandRepository, categoryRepository,
		                                               productRepository, voucherRepository, orderRepository,
		                                               brandMapper, categoryMapper, productMapper, voucherMapper,
		                                               orderMapper, objectMapper);
	}

	private static ReadModelDocument document(ReadModelType type, Long id, Long ownerId, Map<String, Object> payload) {
		ReadModelDocument document = new ReadModelDocument();
		document.setId(ReadModelDocument.documentId(type, id));
		document.setModel(type.name());
		document.setEntityId(id);
		document.setOwnerId(ownerId);
		document.setPayload(payload);
		return document;
	}

	private static Brand brand(Long id) {
		Brand brand = new Brand();
		brand.setId(id);
		return brand;
	}

	private static Category category(Long id) {
		Category category = new Category();
		category.setId(id);
		return category;
	}

	private static Product product(Long id) {
		Product product = new Product();
		product.setId(id);
		return product;
	}

	private static Voucher voucher(Long id) {
		Voucher voucher = new Voucher();
		voucher.setId(id);
		voucher.setCode("SALE15");
		return voucher;
	}

	private static Order order(Long id, Long userId) {
		User user = new User();
		user.setId(userId);
		Order order = new Order();
		order.setId(id);
		order.setOrderCode(555L);
		order.setUser(user);
		return order;
	}
}
