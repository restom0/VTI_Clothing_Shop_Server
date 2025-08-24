package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.exceptions.ConflictException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.CategoryMapper;
import vn.vti.clothing_shop.repositories.CategoryRepository;
import vn.vti.clothing_shop.services.interfaces.CategoryService;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    //@Cacheable(value = "categories")
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::entityToDTO)
                .toList();
    }

    //@CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public CategoryDTO addCategory(CategoryCreateRequest categoryCreateRequest) throws WrapperException {
        try {
            if (categoryRepository.existsByDeletedAtIsNullAndName(categoryCreateRequest.name())) {
                throw new ConflictException("Category already exists");
            }
            final Category category = categoryMapper.createRequestToEntity(categoryCreateRequest);
            return categoryMapper.entityToDTO(categoryRepository.save(category));
        } catch (ConflictException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CachePut(value = "categories")
    @Transactional
    public CategoryDTO updateCategory(CategoryUpdateRequest categoryUpdateRequest, Long id) throws WrapperException {
        try {
            if (categoryRepository.existsByDeletedAtIsNullAndNameAndIdNot(categoryUpdateRequest.name(), id)) {
                throw new ConflictException("Category already exists");
            }
            Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
            return categoryMapper.entityToDTO(categoryRepository.save(categoryMapper.updateRequestToEntity(categoryUpdateRequest, category)));
        } catch (ConflictException | NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public void deleteCategory(Long id) throws WrapperException {
        try {
            Category category = categoryRepository.findByDeletedAtIsNullAndId(id).orElseThrow(() -> new NotFoundException("Category not found"));
            category.setDeletedAt(Instant.now().toEpochMilli());
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@Cacheable(value = "categories", key = "#id")
    public CategoryDTO getCategoryById(Long id) throws WrapperException {
        try {
            Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
            return categoryMapper.entityToDTO(category);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }

    }

    public Long countCategory() {
        return categoryRepository.count();
    }
}
