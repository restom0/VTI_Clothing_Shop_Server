package vn.vti.clothing_shop.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.repositories.BrandRepository;
import vn.vti.clothing_shop.repositories.CategoryRepository;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.repositories.ProductRepository;
import vn.vti.clothing_shop.repositories.VoucherRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "read-model.bootstrap", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ReadModelBootstrapRunner implements ApplicationRunner {
	private final PostgresToMongoReadModelSyncService syncService;
	private final BrandRepository brandRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final VoucherRepository voucherRepository;
	private final OrderRepository orderRepository;

	/** Runs service request. */
	@Override
	public void run(ApplicationArguments args) {
		try {
			brandRepository.findByDeletedAtIsNullOrderByIdDesc()
			               .forEach(brand -> syncService.sync(ReadModelType.BRAND, brand.getId()));
			categoryRepository.findAllByDeletedAtIsNullOrderByIdDesc()
			                  .forEach(category -> syncService.sync(ReadModelType.CATEGORY, category.getId()));
			productRepository.findByDeletedAtIsNullOrderByIdDesc()
			                 .forEach(product -> syncService.sync(ReadModelType.PRODUCT, product.getId()));
			voucherRepository.findByDeletedAtIsNullOrderByIdDesc()
			                 .forEach(voucher -> syncService.sync(ReadModelType.VOUCHER, voucher.getId()));
			orderRepository.findByDeletedAtIsNullOrderByIdDesc()
			               .forEach(order -> syncService.sync(ReadModelType.ORDER, order.getId()));
		} catch (RuntimeException ex) {
			log.warn("Mongo read model bootstrap skipped", ex);
		}
	}
}
