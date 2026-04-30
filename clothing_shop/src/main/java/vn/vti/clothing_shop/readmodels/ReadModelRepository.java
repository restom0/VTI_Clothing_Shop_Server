package vn.vti.clothing_shop.readmodels;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadModelRepository extends MongoRepository<ReadModelDocument, String> {
	List<ReadModelDocument> findByModelAndDeletedFalseOrderBySortValueDesc(String model);

	List<ReadModelDocument> findByModelAndOwnerIdAndDeletedFalseOrderBySortValueDesc(String model, Long ownerId);

	Optional<ReadModelDocument> findByModelAndEntityIdAndDeletedFalse(String model, Long entityId);

	Optional<ReadModelDocument> findByModelAndEntityIdAndOwnerIdAndDeletedFalse(String model, Long entityId, Long ownerId);

	Optional<ReadModelDocument> findByModelAndLookupKeyAndDeletedFalse(String model, String lookupKey);
}
