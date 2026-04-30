package vn.vti.clothing_shop.responses;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageResolver {
    private static MessageSource messageSource;

    public MessageResolver(MessageSource messageSource) {
        MessageResolver.messageSource = messageSource;
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
        if (message == null || message.isBlank() || messageSource == null) {
            return message;
        }

        String key = normalizeKey(message);
        try {
            return messageSource.getMessage(key, args, locale);
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
