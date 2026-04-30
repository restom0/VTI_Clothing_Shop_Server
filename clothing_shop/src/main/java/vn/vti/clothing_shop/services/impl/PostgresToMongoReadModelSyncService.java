package vn.vti.clothing_shop.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
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

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresToMongoReadModelSyncService {
	private final ReadModelRepository readModelRepository;
	private final BrandRepository brandRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final VoucherRepository voucherRepository;
	private final OrderRepository orderRepository;
	private final BrandMapper brandMapper;
	private final CategoryMapper categoryMapper;
	private final ProductMapper productMapper;
	private final VoucherMapper voucherMapper;
	private final OrderMapper orderMapper;
	private final ObjectMapper objectMapper;

	public void syncAfterCommit(ReadModelType modelType, Long entityId) {
		runAfterCommit(() -> sync(modelType, entityId));
	}

	public void removeAfterCommit(ReadModelType modelType, Long entityId) {
		runAfterCommit(() -> remove(modelType, entityId));
	}

	private void runAfterCommit(Runnable runnable) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					runnable.run();
				}
			});
			return;
		}
		runnable.run();
	}

	public void remove(ReadModelType modelType, Long entityId) {
		if (entityId == null) {
			return;
		}
		try {
			readModelRepository.deleteById(ReadModelDocument.documentId(modelType, entityId));
		} catch (DataAccessException ex) {
			log.warn("Cannot remove {} {} from Mongo read model", modelType, entityId, ex);
		}
	}

	public void syncAllProductsAfterCommit() {
		runAfterCommit(() -> productRepository.findByDeletedAtIsNullOrderByIdDesc()
		                                      .forEach(product -> syncProduct(product.getId())));
	}

	private void syncProduct(Long id) {
		Optional<Product> product = productRepository.findByIdAndDeletedAtIsNull(id);
		product.ifPresentOrElse(
				value -> save(ReadModelType.PRODUCT, value.getId(), null, null, value.getId(), productMapper.entityToDTO(value)),
				() -> remove(ReadModelType.PRODUCT, id)
		);
	}

	private void save(ReadModelType modelType, Long entityId, Long ownerId, String lookupKey, Long sortValue, Object payload) {
		ReadModelDocument document = new ReadModelDocument();
		document.setId(ReadModelDocument.documentId(modelType, entityId));
		document.setModel(modelType.name());
		document.setEntityId(entityId);
		document.setOwnerId(ownerId);
		document.setLookupKey(lookupKey);
		document.setSortValue(sortValue);
		document.setDeleted(false);
		document.setSyncedAt(Instant.now().toEpochMilli());
		document.setPayload(toMap(payload));
		readModelRepository.save(document);
	}

	private Map<String, Object> toMap(Object value) {
		return objectMapper.convertValue(value, new TypeReference<>() {
		});
	}

	public void sync(ReadModelType modelType, Long entityId) {
		if (entityId == null) {
			return;
		}
		try {
			switch (modelType) {
				case BRAND -> syncBrand(entityId);
				case CATEGORY -> syncCategory(entityId);
				case PRODUCT -> syncProduct(entityId);
				case VOUCHER -> syncVoucher(entityId);
				case ORDER -> syncOrder(entityId);
			}
		} catch (DataAccessException ex) {
			log.warn("Cannot sync {} {} to Mongo read model", modelType, entityId, ex);
		}
	}

	private void syncBrand(Long id) {
		Optional<Brand> brand = brandRepository.findByDeletedAtIsNullAndId(id);
		brand.ifPresentOrElse(
				value -> save(ReadModelType.BRAND, value.getId(), null, null, value.getId(), brandMapper.entityToDTO(value)),
				() -> remove(ReadModelType.BRAND, id)
		);
	}

	private void syncCategory(Long id) {
		Optional<Category> category = categoryRepository.findByDeletedAtIsNullAndId(id);
		category.ifPresentOrElse(
				value -> save(ReadModelType.CATEGORY, value.getId(), null, null, value.getId(),
				              categoryMapper.entityToDTO(value)),
				() -> remove(ReadModelType.CATEGORY, id)
		);
	}

	private void syncVoucher(Long id) {
		Optional<Voucher> voucher = voucherRepository.findByDeletedAtIsNullAndId(id);
		voucher.ifPresentOrElse(
				value -> {
					VoucherDTO dto = voucherMapper.entityToDTO(value);
					save(ReadModelType.VOUCHER, value.getId(), null, value.getCode(), value.getId(), dto);
				},
				() -> remove(ReadModelType.VOUCHER, id)
		);
	}

	private void syncOrder(Long id) {
		Optional<Order> order = orderRepository.findByDeletedAtIsNullAndId(id);
		order.ifPresentOrElse(
				value -> {
					OrderDTO dto = orderMapper.entityToDTO(value);
					Long ownerId = value.getUser() == null ? null : value.getUser().getId();
					save(ReadModelType.ORDER, value.getId(), ownerId, String.valueOf(value.getOrderCode()), value.getId(), dto);
				},
				() -> remove(ReadModelType.ORDER, id)
		);
	}
}
