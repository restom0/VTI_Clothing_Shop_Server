package vn.vti.clothing_shop.configs;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomRedisSerializerTest {
	private final CustomRedisSerializer serializer = new CustomRedisSerializer();

	@Test
	void serializeAndDeserializeEmptyValues() {
		assertThat(serializer.serialize(null)).isEmpty();
		assertThat(serializer.deserialize(null)).isNull();
		assertThat(serializer.deserialize(new byte[0])).isNull();
	}

	@Test
	void roundTripsTypedPayloadsIncludingJavaTimeValues() {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("name", "VTI");
		payload.put("date", LocalDate.of(2026, 7, 16));

		Object restored = serializer.deserialize(serializer.serialize(payload));

		assertThat(restored).isInstanceOf(Map.class);
		@SuppressWarnings("unchecked")
		Map<Object, Object> restoredMap = (Map<Object, Object>) restored;
		assertThat(restoredMap).containsEntry("name", "VTI");
		assertThat(restoredMap).containsEntry("date", LocalDate.of(2026, 7, 16));
	}

	@Test
	void invalidJsonRaisesSerializationException() {
		byte[] invalidJson = "not-json".getBytes(StandardCharsets.UTF_8);

		assertThatThrownBy(() -> serializer.deserialize(invalidJson))
				.isInstanceOf(SerializationException.class)
				.hasMessageContaining("Could not read JSON");
	}
}
