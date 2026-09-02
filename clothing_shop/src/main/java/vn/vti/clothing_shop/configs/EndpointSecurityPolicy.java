package vn.vti.clothing_shop.configs;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EndpointSecurityPolicy {
	/** Checks whether endpoint requires authentication. */
	public boolean requiresAuthentication(String path, String method) {
		String normalizedPath = normalize(path);
		if (requiresAdmin(normalizedPath)) {
			return true;
		}
		if (startsWithAny(normalizedPath, "/chat", "/order", "/order-item", "/order-items")) {
			return true;
		}
		if (normalizedPath.equals("/user/profile") || startsWithAny(normalizedPath, "/user/password")) {
			return true;
		}
		if (startsWithAny(normalizedPath, "/user") && isWriteMethod(method)) {
			return !normalizedPath.equals("/user/login") && !normalizedPath.equals("/user/register");
		}
		return isWriteMethod(method) && startsWithAny(normalizedPath,
		                                             "/brand",
		                                             "/brands",
		                                             "/category",
		                                             "/categories",
		                                             "/comment",
		                                             "/imported-product",
		                                             "/input-sale",
		                                             "/on-sale-product",
		                                             "/product",
		                                             "/voucher"
		);
	}

	/** Checks whether endpoint requires admin. */
	public boolean requiresAdmin(String path) {
		return startsWithAny(normalize(path), "/audit", "/log");
	}

	/** Normalizes path. */
	public String normalize(String path) {
		if (path == null || path.isBlank()) {
			return "/";
		}
		String normalizedPath = path.toLowerCase(Locale.ROOT);
		if (normalizedPath.length() > 1 && normalizedPath.endsWith("/")) {
			normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
		}
		return normalizedPath;
	}

	private boolean isWriteMethod(String method) {
		return HttpMethod.POST.matches(method)
				|| HttpMethod.PUT.matches(method)
				|| HttpMethod.PATCH.matches(method)
				|| HttpMethod.DELETE.matches(method);
	}

	private boolean startsWithAny(String path, String... prefixes) {
		for (String prefix : prefixes) {
			if (path.equals(prefix) || path.startsWith(prefix + "/")) {
				return true;
			}
		}
		return false;
	}
}
