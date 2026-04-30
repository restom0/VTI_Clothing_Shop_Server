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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String path = normalizedPath(request);
        if (!requiresAuthentication(request, path)) {
            return true;
        }

        try {
            User user = authenticate(request);
            if (requiresAdmin(path) && user.getRole() != UserRole.ADMIN) {
                writeError(response, request.getLocale(), HttpStatus.FORBIDDEN, "messages.auth.forbidden");
                return false;
            }
            return true;
        } catch (Exception exception) {
            writeError(response, request.getLocale(), HttpStatus.UNAUTHORIZED, exception.getMessage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContextHolder.clearContext();
    }

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

    private boolean requiresAuthentication(HttpServletRequest request, String path) {
        if (requiresAdmin(path)) {
            return true;
        }
        if (startsWithAny(path, "/chat", "/order", "/order-item", "/order-items")) {
            return true;
        }
        if (path.equals("/user/profile") || startsWithAny(path, "/user/password")) {
            return true;
        }
        if (startsWithAny(path, "/user") && isWriteRequest(request)) {
            return !path.equals("/user/login") && !path.equals("/user/register");
        }
        return isWriteRequest(request) && startsWithAny(path,
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

    private boolean requiresAdmin(String path) {
        return startsWithAny(path, "/audit", "/log");
    }

    private boolean isWriteRequest(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod())
                || HttpMethod.PUT.matches(request.getMethod())
                || HttpMethod.PATCH.matches(request.getMethod())
                || HttpMethod.DELETE.matches(request.getMethod());
    }

    private boolean startsWithAny(String path, String... prefixes) {
        for (String prefix : prefixes) {
            if (path.equals(prefix) || path.startsWith(prefix + "/")) {
                return true;
            }
        }
        return false;
    }

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
