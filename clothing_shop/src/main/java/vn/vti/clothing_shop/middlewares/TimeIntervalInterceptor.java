package vn.vti.clothing_shop.middlewares;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import vn.vti.clothing_shop.responses.TimeIntervalContext;

import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;
import java.util.Set;

@Component
public class TimeIntervalInterceptor implements HandlerInterceptor {
	private static final Set<String> SUPPORTED_LANGUAGE_CODES = Set.of("en", "es", "de", "fr", "ca", "it");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String languageCode = resolveLanguageCode(request);
		if (languageCode != null) {
			Locale locale = Locale.forLanguageTag(languageCode);
			LocaleContextHolder.setLocale(locale);
			TimeIntervalContext.enable(locale);
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		TimeIntervalContext.clear();
		LocaleContextHolder.resetLocaleContext();
	}

	private String resolveLanguageCode(HttpServletRequest request) {
		String explicitCode = firstNotBlank(
				request.getParameter("languageCode"),
				request.getParameter("lang"),
				request.getParameter("language"),
				request.getHeader("X-Language-Code")
		);
		if (explicitCode != null) {
			return normalize(explicitCode);
		}
		return normalizeAcceptLanguage(request.getHeader("Accept-Language"));
	}

	private String normalizeAcceptLanguage(String header) {
		if (header == null || header.isBlank()) {
			return null;
		}
		try {
			List<LanguageRange> ranges = LanguageRange.parse(header);
			for (LanguageRange range : ranges) {
				String normalized = normalize(range.getRange());
				if (normalized != null) {
					return normalized;
				}
			}
		} catch (IllegalArgumentException ignored) {
			return normalize(header);
		}
		return null;
	}

	private String normalize(String languageCode) {
		if (languageCode == null || languageCode.isBlank()) {
			return null;
		}
		String primaryLanguage = Locale.forLanguageTag(languageCode.trim()).getLanguage();
		return SUPPORTED_LANGUAGE_CODES.contains(primaryLanguage) ? primaryLanguage : "en";
	}

	private String firstNotBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return null;
	}
}
