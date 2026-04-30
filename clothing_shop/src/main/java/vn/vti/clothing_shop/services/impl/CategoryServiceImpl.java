package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.exceptions.ConflictException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.CategoryMapper;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.repositories.CategoryRepository;
import vn.vti.clothing_shop.services.interfaces.CategoryService;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;
	private final MongoReadModelQueryService readModelQueryService;
	private final PostgresToMongoReadModelSyncService readModelSyncService;

	@Cacheable(value = "categories", key = "'all'")
	@Override
	public List<CategoryDTO> getAllCategories() {
		List<CategoryDTO> mongoCategories = readModelQueryService.findAll(ReadModelType.CATEGORY, CategoryDTO.class);
		if (mongoCategories != null && !mongoCategories.isEmpty()) {
			return mongoCategories;
		}
		return categoryRepository.findAllByDeletedAtIsNullOrderByIdDesc().stream()
		                         .map(categoryMapper::entityToDTO)
		                         .toList();
	}

	@Caching(evict = {
			@CacheEvict(value = "categories", allEntries = true),
			@CacheEvict(value = "products", allEntries = true)
	})
	@Transactional
	@Override
	public CategoryDTO addCategory(CategoryCreateRequest categoryCreateRequest) throws WrapperException {
		try {
			if (categoryRepository.existsByDeletedAtIsNullAndName(categoryCreateRequest.name())) {
				throw new ConflictException("messages.categories.exists");
			}
			final Category category = categoryMapper.createRequestToEntity(categoryCreateRequest);
			Category savedCategory = categoryRepository.save(category);
			readModelSyncService.syncAfterCommit(ReadModelType.CATEGORY, savedCategory.getId());
			return categoryMapper.entityToDTO(savedCategory);
		} catch (ConflictException ex) {
			throw new WrapperException(ex);
		}
	}

	@CacheEvict(value = "categories", allEntries = true)
	@Transactional
	@Override
	public CategoryDTO updateCategory(CategoryUpdateRequest categoryUpdateRequest, Long id) throws WrapperException {
		try {
			if (categoryRepository.existsByDeletedAtIsNullAndNameAndIdNot(categoryUpdateRequest.name(), id)) {
				throw new ConflictException("messages.categories.exists");
			}
			Category category = categoryRepository.findById(id).orElseThrow(
					() -> new NotFoundException("messages.categories.notfound"));
			Category savedCategory = categoryRepository.save(
					categoryMapper.updateRequestToEntity(categoryUpdateRequest, category));
			readModelSyncService.syncAfterCommit(ReadModelType.CATEGORY, savedCategory.getId());
			readModelSyncService.syncAllProductsAfterCommit();
			return categoryMapper.entityToDTO(savedCategory);
		} catch (ConflictException | NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	@Caching(evict = {
			@CacheEvict(value = "categories", allEntries = true),
			@CacheEvict(value = "products", allEntries = true)
	})
	@Transactional
	@Override
	public void deleteCategory(Long id) throws WrapperException {
		try {
			Category category = categoryRepository.findByDeletedAtIsNullAndId(id).orElseThrow(
					() -> new NotFoundException("messages.categories.notfound"));
			category.setDeletedAt(Instant.now().toEpochMilli());
			readModelSyncService.removeAfterCommit(ReadModelType.CATEGORY, id);
			readModelSyncService.syncAllProductsAfterCommit();
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	@Cacheable(value = "categories", key = "'id:' + #id")
	@Override
	public CategoryDTO getCategoryById(Long id) throws WrapperException {
		try {
			var mongoCategory = readModelQueryService.findById(ReadModelType.CATEGORY, id, CategoryDTO.class);
			if (mongoCategory != null && mongoCategory.isPresent()) {
				return mongoCategory.get();
			}
			Category category = categoryRepository.findById(id).orElseThrow(
					() -> new NotFoundException("messages.categories.notfound"));
			return categoryMapper.entityToDTO(category);
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}

	}

	@Override
	public Long countCategory() {
		return categoryRepository.count();
	}
}
