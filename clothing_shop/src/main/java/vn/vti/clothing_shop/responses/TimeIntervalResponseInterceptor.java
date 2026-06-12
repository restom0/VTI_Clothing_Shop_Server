package vn.vti.clothing_shop.responses;

import lombok.RequiredArgsConstructor;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
@RequiredArgsConstructor
public class TimeIntervalResponseInterceptor implements ResponseBodyAdvice<Object> {
	private static final Set<String> TEMPORAL_FIELD_NAMES = Set.of(
			"createdAt",
			"updatedAt",
			"deletedAt",
			"availableDate",
			"startDate",
			"endDate",
			"syncedAt"
	);

	private final TimeIntervalFormatter formatter;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body,
	                              MethodParameter returnType,
	                              MediaType selectedContentType,
	                              Class<? extends HttpMessageConverter<?>> selectedConverterType,
	                              org.springframework.http.server.ServerHttpRequest request,
	                              org.springframework.http.server.ServerHttpResponse response) {
		if (!TimeIntervalContext.isEnabled() || body == null || !isJson(selectedContentType)) {
			return body;
		}
		if (!(body instanceof BaseMessageResponse)) {
			return body;
		}
		return transform(body, null, TimeIntervalContext.locale(), new IdentityHashMap<>());
	}

	private boolean isJson(MediaType mediaType) {
		return mediaType == null
				|| MediaType.APPLICATION_JSON.includes(mediaType)
				|| mediaType.getSubtype().endsWith("+json");
	}

	private Object transform(Object value, String fieldName, Locale locale, IdentityHashMap<Object, Boolean> visited) {
		if (value == null) {
			return null;
		}
		if (isTemporalField(fieldName) && isTemporalValue(value)) {
			return formatter.formatTemporal(value, locale);
		}
		if (isSimpleValue(value)) {
			return value;
		}
		if (visited.containsKey(value)) {
			return null;
		}
		visited.put(value, Boolean.TRUE);
		if (value instanceof Map<?, ?> map) {
			return transformMap(map, locale, visited);
		}
		if (value instanceof Collection<?> collection) {
			return transformCollection(collection, locale, visited);
		}
		if (value.getClass().isArray()) {
			return transformArray(value, locale, visited);
		}
		return transformObject(value, locale, visited);
	}

	private Map<String, Object> transformMap(Map<?, ?> map, Locale locale, IdentityHashMap<Object, Boolean> visited) {
		Map<String, Object> transformed = new LinkedHashMap<>();
		map.forEach((key, value) -> {
			String fieldName = String.valueOf(key);
			transformed.put(fieldName, transform(value, fieldName, locale, visited));
		});
		return transformed;
	}

	private List<Object> transformCollection(Collection<?> collection, Locale locale, IdentityHashMap<Object, Boolean> visited) {
		List<Object> transformed = new ArrayList<>(collection.size());
		for (Object item : collection) {
			transformed.add(transform(item, null, locale, visited));
		}
		return transformed;
	}

	private List<Object> transformArray(Object array, Locale locale, IdentityHashMap<Object, Boolean> visited) {
		int length = Array.getLength(array);
		List<Object> transformed = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			transformed.add(transform(Array.get(array, i), null, locale, visited));
		}
		return transformed;
	}

	private Map<String, Object> transformObject(Object value, Locale locale, IdentityHashMap<Object, Boolean> visited) {
		Map<String, Object> transformed = new LinkedHashMap<>();
		for (Field field : fieldsOf(value.getClass())) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			field.setAccessible(true);
			try {
				transformed.put(field.getName(), transform(field.get(value), field.getName(), locale, visited));
			} catch (IllegalAccessException ignored) {
				transformed.put(field.getName(), null);
			}
		}
		return transformed;
	}

	private List<Field> fieldsOf(Class<?> type) {
		List<Field> fields = new ArrayList<>();
		Class<?> current = type;
		while (current != null && current != Object.class) {
			fields.addAll(List.of(current.getDeclaredFields()));
			current = current.getSuperclass();
		}
		return fields;
	}

	private boolean isTemporalField(String fieldName) {
		return fieldName != null && TEMPORAL_FIELD_NAMES.contains(fieldName);
	}

	private boolean isTemporalValue(Object value) {
		return value instanceof Long
				|| value instanceof LocalDate
				|| value instanceof LocalDateTime
				|| value instanceof OffsetDateTime
				|| value instanceof Instant;
	}

	private boolean isSimpleValue(Object value) {
		return value instanceof String
				|| value instanceof Number
				|| value instanceof Boolean
				|| value instanceof Character
				|| value instanceof Enum<?>
				|| isTemporalValue(value);
	}
}
