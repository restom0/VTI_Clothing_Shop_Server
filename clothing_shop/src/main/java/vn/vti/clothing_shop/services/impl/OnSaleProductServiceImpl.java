package vn.vti.clothing_shop.services.impl;

import lombok.AllArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.repositories.OnSaleProductRepository;
import vn.vti.clothing_shop.services.interfaces.OnSaleProductService;

import java.util.List;

@AllArgsConstructor
@Service
public class OnSaleProductServiceImpl implements OnSaleProductService {

	private final OnSaleProductRepository onSaleProductRepository;

	@Cacheable(value = "onSaleProducts", key = "'all'")
	@Override
	public List<OnSaleProduct> getAllOnSaleProducts() {
		return onSaleProductRepository.findDistinctByDeletedAtIsNull();
	}

	@Override
	@Cacheable(value = "onSaleProducts", key = "'product:' + #id")
	public List<OnSaleProduct> getOnSaleProductById(Long id) {
		return onSaleProductRepository.findAllByProductId(id);
	}
}


