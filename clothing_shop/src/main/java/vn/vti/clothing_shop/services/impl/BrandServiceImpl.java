package vn.vti.clothing_shop.services.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import vn.vti.clothing_shop.constants.Messages;
import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.exceptions.ConflictException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.BrandMapper;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.repositories.BrandRepository;
import vn.vti.clothing_shop.services.interfaces.BrandService;

@Service
@AllArgsConstructor
public class BrandServiceImpl implements BrandService {

	private final BrandRepository brandRepository;
	private final BrandMapper brandMapper;
	private final MongoReadModelQueryService readModelQueryService;
	private final PostgresToMongoReadModelSyncService readModelSyncService;

	@Cacheable(value = "brands", key = "'all'")
	public List<BrandDTO> getBrands() {
		List<BrandDTO> mongoBrands = readModelQueryService.findAll(ReadModelType.BRAND, BrandDTO.class);
		if (mongoBrands != null && !mongoBrands.isEmpty()) {
			return mongoBrands;
		}
		return brandRepository.findByDeletedAtIsNullOrderByIdDesc()
		                      .stream()
		                      .map(brandMapper::entityToDTO)
		                      .toList();
	}

	@CacheEvict(value = "brands", allEntries = true)
	@Transactional
	public BrandDTO createBrand(BrandCreateRequest brandCreateRequest) throws WrapperException {
		try {
			if (brandRepository.existsByDeletedAtIsNullAndName(brandCreateRequest.name())) {
				throw new ConflictException("messages.brands.exists");
			}
			final Brand newBrand = brandMapper.createRequestToEntity(brandCreateRequest);
			Brand savedBrand = brandRepository.save(newBrand);
			readModelSyncService.syncAfterCommit(ReadModelType.BRAND, savedBrand.getId());
			return brandMapper.entityToDTO(savedBrand);
		} catch (ConflictException e) {
			throw new WrapperException(e);
		}
	}

	@Caching(evict = {
			@CacheEvict(value = "brands", allEntries = true),
			@CacheEvict(value = "products", allEntries = true)
	})
	@Transactional
	@Override
	public BrandDTO updateBrand(BrandUpdateRequest brandUpdateRequest, Long id) throws WrapperException {
		try {
			Brand oldBrand = brandRepository.findById(id)
			                                .orElseThrow(() -> new NotFoundException(Messages.MESSAGE_BRAND_NOTFOUND));
			if (brandRepository.existsByDeletedAtIsNullAndIdNotAndName(id, brandUpdateRequest.name())) {
				throw new ConflictException("messages.brands.exists");
			}
			final Brand newBrand = brandMapper.updateRequestToEntity(brandUpdateRequest, oldBrand);
			Brand savedBrand = brandRepository.save(newBrand);
			readModelSyncService.syncAfterCommit(ReadModelType.BRAND, savedBrand.getId());
			readModelSyncService.syncAllProductsAfterCommit();
			return brandMapper.entityToDTO(savedBrand);
		} catch (NotFoundException | ConflictException e) {
			throw new WrapperException(e);
		}
	}

	@Caching(evict = {
			@CacheEvict(value = "brands", allEntries = true),
			@CacheEvict(value = "products", allEntries = true)
	})
	@Transactional
	@Override
	public void deleteBrand(Long id) throws WrapperException {
		try {
			Brand brand = brandRepository.findByDeletedAtIsNullAndId(id).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_BRAND_NOTFOUND));
			brand.setDeletedAt(Instant.now().toEpochMilli());
			brandRepository.save(brand);
			readModelSyncService.removeAfterCommit(ReadModelType.BRAND, id);
			readModelSyncService.syncAllProductsAfterCommit();
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	@Cacheable(value = "brands", key = "'id:' + #id")
	@Override
	public BrandDTO findBrandById(Long id) throws WrapperException {
		try {
			var mongoBrand = readModelQueryService.findById(ReadModelType.BRAND, id, BrandDTO.class);
			if (mongoBrand != null && mongoBrand.isPresent()) {
				return mongoBrand.get();
			}
			final Brand brand = brandRepository.findByDeletedAtIsNullAndId(id)
			                                   .orElseThrow(() -> new NotFoundException(Messages.MESSAGE_BRAND_NOTFOUND));
			return brandMapper.entityToDTO(brand);
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	@Override
	public Long countBrand() {
		return brandRepository.countByDeletedAtIsNull();
	}
}
