package vn.vti.clothing_shop.responses;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class MessageResolver {
	private static final AtomicReference<MessageSource> MESSAGE_SOURCE = new AtomicReference<>();

	MessageResolver(MessageSource messageSource) {
		MESSAGE_SOURCE.set(messageSource);
	}

	public static Object resolveIfMessageKey(Object value) {
		if (value instanceof String message) {
			return resolve(message);
		}
		return value;
	}

	public static String resolve(String message) {
		return resolve(LocaleContextHolder.getLocale(), message);
	}

	public static String resolve(String message, Object... args) {
		return resolve(LocaleContextHolder.getLocale(), message, args);
	}

	public static String resolve(Locale locale, String message, Object... args) {
		MessageSource source = MESSAGE_SOURCE.get();
		if (message == null || message.isBlank() || source == null) {
			return message;
		}

		String key = normalizeKey(message);
		try {
			return source.getMessage(key, args, locale);
		} catch (NoSuchMessageException exception) {
			return message;
		}
	}

	private static String normalizeKey(String message) {
		if (message.startsWith("{") && message.endsWith("}") && message.length() > 2) {
			return message.substring(1, message.length() - 1);
		}
		return message;
	}
}
