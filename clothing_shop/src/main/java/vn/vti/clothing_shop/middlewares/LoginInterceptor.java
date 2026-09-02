package vn.vti.clothing_shop.middlewares;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.UnauthorizeException;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.responses.ExceptionMessageResponse;
import vn.vti.clothing_shop.responses.MessageResolver;
import vn.vti.clothing_shop.configs.EndpointSecurityPolicy;
import vn.vti.clothing_shop.services.JwtService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
	private static final String BEARER_PREFIX = "Bearer ";

	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final ObjectMapper objectMapper;
	private final EndpointSecurityPolicy endpointSecurityPolicy;

	/** Runs handle. */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (HttpMethod.OPTIONS.matches(request.getMethod())) {
			return true;
		}

		String path = normalizedPath(request);
		if (!endpointSecurityPolicy.requiresAuthentication(path, request.getMethod())) {
			return true;
		}

		try {
			User user = authenticate(request);
			if (endpointSecurityPolicy.requiresAdmin(path) && user.getRole() != UserRole.ADMIN) {
				writeError(response, request.getLocale(), HttpStatus.FORBIDDEN, "messages.auth.forbidden");
				return false;
			}
			return true;
		} catch (Exception exception) {
			writeError(response, request.getLocale(), HttpStatus.UNAUTHORIZED, exception.getMessage());
			return false;
		}
	}

	/** Runs completion. */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		SecurityContextHolder.clearContext();
	}

	/** Handles authenticate. */
	private User authenticate(HttpServletRequest request) throws UnauthorizeException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			throw new UnauthorizeException("messages.auth.required");
		}

		String jwt = authHeader.substring(BEARER_PREFIX.length());
		if (jwtService.isTokenExpired(jwt)) {
			throw new UnauthorizeException("messages.auth.tokenExpired");
		}

		String userId = jwtService.extractId(jwt);
		User user = userRepository.findById(Long.parseLong(userId))
		                          .orElseThrow(() -> new UnauthorizeException("messages.users.notfound"));
		if (!jwtService.isTokenValid(jwt, user)) {
			throw new UnauthorizeException("messages.auth.invalidToken");
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
			List<GrantedAuthority> authorities = List.of(
					new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
			);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}
		return user;
	}

	/** Handles normalized path. */
	private String normalizedPath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String path = request.getRequestURI();
		if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}
		if (path.startsWith("/api/")) {
			path = path.substring("/api".length());
		}
		if (path.length() > 1 && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path.toLowerCase(Locale.ROOT);
	}

	/** Handles write error. */
	private void writeError(HttpServletResponse response, Locale locale, HttpStatus status, String message) throws IOException {
		response.setStatus(status.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		ExceptionMessageResponse body = new ExceptionMessageResponse(
				status.value(),
				MessageResolver.resolve(locale, message)
		);
		response.getWriter().write(objectMapper.writeValueAsString(body));
	}
}
