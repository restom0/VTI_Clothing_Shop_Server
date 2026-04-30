package vn.vti.clothing_shop.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.readmodels.ReadModelDocument;
import vn.vti.clothing_shop.readmodels.ReadModelRepository;
import vn.vti.clothing_shop.readmodels.ReadModelType;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MongoReadModelQueryService {
	private final ReadModelRepository readModelRepository;
	private final ObjectMapper objectMapper;

	public <T> List<T> findAll(ReadModelType modelType, Class<T> targetType) {
		try {
			return readModelRepository.findByModelAndDeletedFalseOrderBySortValueDesc(modelType.name())
			                          .stream()
			                          .map(document -> toPayload(document, targetType))
			                          .flatMap(Optional::stream)
			                          .toList();
		} catch (DataAccessException ex) {
			log.warn("Mongo read model unavailable for {}", modelType, ex);
			return List.of();
		}
	}

	private <T> Optional<T> toPayload(ReadModelDocument document, Class<T> targetType) {
		try {
			return Optional.ofNullable(objectMapper.convertValue(document.getPayload(), targetType));
		} catch (IllegalArgumentException ex) {
			log.warn("Cannot convert Mongo read model {} id {} to {}",
			         document.getModel(), document.getEntityId(), targetType.getSimpleName(), ex);
			return Optional.empty();
		}
	}

	public <T> List<T> findByOwner(ReadModelType modelType, Long ownerId, Class<T> targetType) {
		try {
			return readModelRepository.findByModelAndOwnerIdAndDeletedFalseOrderBySortValueDesc(modelType.name(), ownerId)
			                          .stream()
			                          .map(document -> toPayload(document, targetType))
			                          .flatMap(Optional::stream)
			                          .toList();
		} catch (DataAccessException ex) {
			log.warn("Mongo read model unavailable for {} owner {}", modelType, ownerId, ex);
			return List.of();
		}
	}

	public <T> Optional<T> findById(ReadModelType modelType, Long id, Class<T> targetType) {
		try {
			return readModelRepository.findByModelAndEntityIdAndDeletedFalse(modelType.name(), id)
			                          .flatMap(document -> toPayload(document, targetType));
		} catch (DataAccessException ex) {
			log.warn("Mongo read model unavailable for {} id {}", modelType, id, ex);
			return Optional.empty();
		}
	}

	public <T> Optional<T> findByIdAndOwner(ReadModelType modelType, Long id, Long ownerId, Class<T> targetType) {
		try {
			return readModelRepository.findByModelAndEntityIdAndOwnerIdAndDeletedFalse(modelType.name(), id, ownerId)
			                          .flatMap(document -> toPayload(document, targetType));
		} catch (DataAccessException ex) {
			log.warn("Mongo read model unavailable for {} id {} owner {}", modelType, id, ownerId, ex);
			return Optional.empty();
		}
	}

	public <T> Optional<T> findByLookupKey(ReadModelType modelType, String lookupKey, Class<T> targetType) {
		try {
			return readModelRepository.findByModelAndLookupKeyAndDeletedFalse(modelType.name(), lookupKey)
			                          .flatMap(document -> toPayload(document, targetType));
		} catch (DataAccessException ex) {
			log.warn("Mongo read model unavailable for {} key {}", modelType, lookupKey, ex);
			return Optional.empty();
		}
	}
}
