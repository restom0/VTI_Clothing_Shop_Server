package vn.vti.clothing_shop.readmodels;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadModelRepository extends MongoRepository<ReadModelDocument, String> {
	/** Finds by model and deleted false order by sort value desc. */
	List<ReadModelDocument> findByModelAndDeletedFalseOrderBySortValueDesc(String model);

	/** Finds by model and owner id and deleted false order by sort value desc. */
	List<ReadModelDocument> findByModelAndOwnerIdAndDeletedFalseOrderBySortValueDesc(String model, Long ownerId);

	/** Finds by model and entity id and deleted false. */
	Optional<ReadModelDocument> findByModelAndEntityIdAndDeletedFalse(String model, Long entityId);

	/** Finds by model and entity id and owner id and deleted false. */
	Optional<ReadModelDocument> findByModelAndEntityIdAndOwnerIdAndDeletedFalse(String model, Long entityId, Long ownerId);

	/** Finds by model and lookup key and deleted false. */
	Optional<ReadModelDocument> findByModelAndLookupKeyAndDeletedFalse(String model, String lookupKey);
}
