package vn.vti.clothing_shop.services.implementations;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.CategoryCreateDTO;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.*;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.exceptions.ConflictException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.mappers.*;
import vn.vti.clothing_shop.repositories.CategoryRepository;
import vn.vti.clothing_shop.repositories.ColorRepository;
import vn.vti.clothing_shop.repositories.MaterialRepository;
import vn.vti.clothing_shop.repositories.SizeRepository;
import vn.vti.clothing_shop.responses.CategoryResponse;
import vn.vti.clothing_shop.services.interfaces.CategoryService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplementation implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final MaterialRepository materialRepository;
    private final CategoryMapper categoryMapper;
    private final ColorMapper colorMapper;
    private final SizeMapper sizeMapper;
    private final MaterialMapper materialMapper;

    @Autowired
    public CategoryServiceImplementation(CategoryRepository categoryRepository, ColorRepository colorRepository, SizeRepository sizeRepository, MaterialRepository materialRepository, CategoryMapper categoryMapper, ColorMapper colorMapper, SizeMapper sizeMapper, MaterialMapper materialMapper) {
        this.categoryRepository = categoryRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.materialRepository = materialRepository;
        this.categoryMapper = categoryMapper;
        this.colorMapper = colorMapper;
        this.sizeMapper = sizeMapper;
        this.materialMapper = materialMapper;
    }

    //@Cacheable(value = "categories")
    public List<CategoryDTO> getAllCategories() {
        return this.categoryRepository.findAll().stream()
                .map(categoryMapper::EntityToDTO)
                .collect(Collectors.toList());
    }

    //@CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public Boolean addCategory(CategoryCreateDTO categoryCreateDTO) {
        if (this.categoryRepository.findByName(categoryCreateDTO.getName()).isPresent()) {
            throw new ConflictException("Category already exists");
        }
        this.categoryRepository.save(categoryMapper.CategoryCreateDTOToEntity(categoryCreateDTO));
        return true;
    };

    //@CachePut(value = "categories")
    @Transactional
    public Boolean updateCategory(CategoryUpdateDTO categoryUpdateDTO){
        Category category = this.categoryRepository.findById(categoryUpdateDTO.getId()).orElseThrow(() -> new NotFoundException("Category not found"));
        if(this.categoryRepository.findByName(categoryUpdateDTO.getName()).isPresent()&& !Objects.equals(category.getId(), categoryUpdateDTO.getId())){
            throw new ConflictException("Category already exists");
        }
        this.categoryRepository.save(categoryMapper.CategoryUpdateDTOToEntity(categoryUpdateDTO,category));
        return true;
    };

    //@CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public Boolean deleteCategory(Long id){
        Category category = this.categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        this.categoryRepository.delete(category);
        return true;
    };

    //@Cacheable(value = "categories", key = "#id")
    public CategoryResponse getCategoryById(Long id) {
        CategoryDTO category = categoryMapper.EntityToDTO(this.categoryRepository.findById(id).orElseThrow(()-> new NotFoundException("Brand not found")));
        List<ColorDTO> colorDTOS = this.colorRepository.findByCategory_id(category.getId()).stream().map(colorMapper::EntityToDTO).toList();
        List<SizeDTO> sizeDTOS = this.sizeRepository.findByCategory_id(category.getId()).stream().map(sizeMapper::EntityToDTO).toList();
        List<MaterialDTO> materialDTOS = this.materialRepository.findByCategory_id(category.getId()).stream().map(materialMapper::EntityToDTO).toList();
        return new CategoryResponse(category, colorDTOS, sizeDTOS, materialDTOS);
    }

    public Long countCategory(){
        return categoryRepository.count();
    }
}
