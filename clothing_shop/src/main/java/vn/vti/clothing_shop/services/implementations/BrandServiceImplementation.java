package vn.vti.clothing_shop.services.implementations;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.BrandCreateDTO;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.dtos.outs.ColorDTO;
import vn.vti.clothing_shop.dtos.outs.MaterialDTO;
import vn.vti.clothing_shop.dtos.outs.SizeDTO;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.exceptions.ConflictException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.mappers.BrandMapper;
import vn.vti.clothing_shop.mappers.ColorMapper;
import vn.vti.clothing_shop.mappers.MaterialMapper;
import vn.vti.clothing_shop.mappers.SizeMapper;
import vn.vti.clothing_shop.repositories.BrandRepository;
import vn.vti.clothing_shop.repositories.ColorRepository;
import vn.vti.clothing_shop.repositories.MaterialRepository;
import vn.vti.clothing_shop.repositories.SizeRepository;
import vn.vti.clothing_shop.responses.BrandResponse;
import vn.vti.clothing_shop.services.interfaces.BrandService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BrandServiceImplementation implements BrandService {

    private final BrandRepository brandRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final MaterialRepository materialRepository;
    private final BrandMapper brandMapper;
    private final ColorMapper colorMapper;
    private final SizeMapper sizeMapper;
    private final MaterialMapper materialMapper;


    @Autowired
    public BrandServiceImplementation(BrandRepository brandRepository, BrandMapper brandMapper, ColorRepository colorRepository, SizeRepository sizeRepository, MaterialRepository materialRepository, ColorMapper colorMapper, SizeMapper sizeMapper, MaterialMapper materialMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.materialRepository = materialRepository;
        this.colorMapper = colorMapper;
        this.sizeMapper = sizeMapper;
        this.materialMapper = materialMapper;
    }
    //@Cacheable(value = "brands")
    public List<BrandDTO> getAllBrands() {
        return this.brandRepository.findAll().stream()
                .map(brandMapper::EntityToDTO)
                .collect(Collectors.toList());
    }

    //@CacheEvict(value = "brands", allEntries = true)
    @Transactional
    public Boolean addBrand(BrandCreateDTO brandCreateDTO) {
        if(this.brandRepository.findByName(brandCreateDTO.getName()).isPresent()){
            throw new ConflictException("Brand already exists");
        }
        this.brandRepository.save(brandMapper.BrandCreateDTOToEntity(brandCreateDTO));
        return true;
    }

    //@CachePut(value = "brands")
    @Transactional
    public Boolean updateBrand(BrandUpdateDTO brandUpdateDTO) {
        Brand brand = this.brandRepository.findById(brandUpdateDTO.getId()).orElseThrow(()-> new NotFoundException("Brand not found"));
        if(this.brandRepository.findByName(brandUpdateDTO.getName()).isPresent()&& !Objects.equals(brand.getId(), brandUpdateDTO.getId()))
        {
            throw new ConflictException("Brand already exists");
        }
        this.brandRepository.save(brandMapper.BrandUpdateDTOToEntity(brandUpdateDTO,brand));
        return true;
    }
    //@CacheEvict(value = "brands", allEntries = true)
    @Transactional
    public Boolean deleteBrand(Long id) {
        Brand brand = this.brandRepository.findById(id).orElseThrow(()-> new NotFoundException("Brand not found"));
        brand.setDeleted_at(LocalDateTime.now());
        this.brandRepository.save(brand);
        return true;
    }

    //@Cacheable(value = "brands", key = "#id")
    public BrandResponse getBrandById(Long id) {
        BrandDTO brand = brandMapper.EntityToDTO(this.brandRepository.findById(id).orElseThrow(()-> new NotFoundException("Brand not found")));
        List<ColorDTO> colorDTOS = this.colorRepository.findAll().stream().map(colorMapper::EntityToDTO).toList();
        List<SizeDTO> sizeDTOS = this.sizeRepository.findAll().stream().map(sizeMapper::EntityToDTO).toList();
        List<MaterialDTO> materialDTOS = this.materialRepository.findAll().stream().map(materialMapper::EntityToDTO).toList();
        return new BrandResponse(brand, colorDTOS, sizeDTOS, materialDTOS);
    }

    public Long countBrand(){
        return brandRepository.count();
    };
}
