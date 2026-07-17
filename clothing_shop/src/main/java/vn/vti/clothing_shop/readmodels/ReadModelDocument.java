package vn.vti.clothing_shop.readmodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "read_models")
@CompoundIndexes({
		@CompoundIndex(name = "idx_model_sort", def = "{'model': 1, 'deleted': 1, 'sortValue': -1}"),
		@CompoundIndex(name = "idx_model_owner_sort", def = "{'model': 1, 'ownerId': 1, 'deleted': 1, 'sortValue': -1}"),
		@CompoundIndex(name = "idx_model_lookup", def = "{'model': 1, 'lookupKey': 1, 'deleted': 1}")
})
public class ReadModelDocument {
	@Id
	private String id;

	@Indexed
	private String model;

	@Indexed
	private Long entityId;

	private Long ownerId;

	private String lookupKey;

	private Long sortValue;

	private boolean deleted;

	private Long syncedAt;

	private Map<String, Object> payload;

	/** Builds id. */
	public static String documentId(ReadModelType modelType, Long entityId) {
		return modelType.name() + ":" + entityId;
	}
}
