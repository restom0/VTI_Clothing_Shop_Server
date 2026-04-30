package vn.vti.clothing_shop.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.outs.OnSaleProductDTO;
import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.mappers.OnSaleProductMapper;
import vn.vti.clothing_shop.repositories.OnSaleProductRepository;
import vn.vti.clothing_shop.services.interfaces.OnSaleProductService;

import java.util.List;

@AllArgsConstructor
@Service
public class OnSaleProductServiceImpl implements OnSaleProductService {

    private final OnSaleProductRepository onSaleProductRepository;
    private final OnSaleProductMapper onSaleProductMapper;

    @Cacheable(value = "onSaleProducts", key = "'all'")
    @Override
    public List<OnSaleProductDTO> getAllOnSaleProducts() {
        final List<OnSaleProduct> onSaleProducts = onSaleProductRepository.findDistinctByDeletedAtIsNull();
        return onSaleProductMapper.entityToDTO(onSaleProducts);
    }

    @Override
    @Cacheable(value = "onSaleProducts", key = "'product:' + #id")
    public List<OnSaleProductDTO> getOnSaleProductById(Long id) {
        return onSaleProductRepository.findAllByProductId(id)
                .stream()
                .map(onSaleProductMapper::entityToDTO)
                .toList();
    }
}


