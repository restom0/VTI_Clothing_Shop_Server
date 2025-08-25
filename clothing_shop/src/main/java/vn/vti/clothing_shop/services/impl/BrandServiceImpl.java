package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.exceptions.ConflictException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.BrandMapper;
import vn.vti.clothing_shop.repositories.BrandRepository;
import vn.vti.clothing_shop.services.interfaces.BrandService;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public List<BrandDTO> getBrands() {
        return brandRepository.findByDeletedAtIsNullOrderByIdDesc()
                .stream()
                .map(brandMapper::entityToDTO)
                .toList();
    }

    //@CacheEvict(value = "brands", allEntries = true)
    @Transactional
    public BrandDTO createBrand(BrandCreateRequest brandCreateRequest) throws WrapperException {
        try {
            if (brandRepository.existsByDeletedAtIsNullAndName(brandCreateRequest.name())) {
                throw new ConflictException("Brand already exists");
            }
            final Brand newBrand = brandMapper.createRequestToEntity(brandCreateRequest);
            return brandMapper.entityToDTO(brandRepository.save(newBrand));
        } catch (ConflictException e) {
            throw new WrapperException(e);
        }
    }

    //@CachePut(value = "brands")
    @Transactional
    @Override
    public BrandDTO updateBrand(BrandUpdateRequest brandUpdateRequest, Long id) throws WrapperException {
        try {
            Brand oldBrand = brandRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Brand not found"));
            if (brandRepository.existsByDeletedAtIsNullAndIdNotAndName(id, brandUpdateRequest.name())) {
                throw new ConflictException("Brand already exists");
            }
            final Brand newBrand = brandMapper.updateRequestToEntity(brandUpdateRequest, oldBrand);
            return brandMapper.entityToDTO(brandRepository.save(newBrand));
        } catch (NotFoundException | ConflictException e) {
            throw new WrapperException(e);
        }
    }

    //@CacheEvict(value = "brands", allEntries = true)
    @Transactional
    @Override
    public void deleteBrand(Long id) throws WrapperException {
        try {
            Brand brand = brandRepository.findByDeletedAtIsNullAndId(id).orElseThrow(() -> new NotFoundException("Brand not found"));
            brand.setDeletedAt(Instant.now().toEpochMilli());
            brandRepository.save(brand);
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    //@Cacheable(value = "brands", key = "#id")
    @Override
    public BrandDTO findBrandById(Long id) throws WrapperException {
        try {
            final Brand brand = brandRepository.findByDeletedAtIsNullAndId(id)
                    .orElseThrow(() -> new NotFoundException("Brand not found"));
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
