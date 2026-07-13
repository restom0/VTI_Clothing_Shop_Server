package vn.vti.clothing_shop.configs;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import vn.vti.clothing_shop.middlewares.OAuth2LoginFailureHandler;
import vn.vti.clothing_shop.middlewares.OAuth2LoginSuccessHandler;
import vn.vti.clothing_shop.services.SocialOAuth2UserService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private static final RequestMatcher BEARER_TOKEN_REQUEST = request -> {
		String authorization = request.getHeader("Authorization");
		return authorization != null && authorization.startsWith("Bearer ");
	};
	private static final RequestMatcher USER_LOGIN = PathPatternRequestMatcher.pathPattern("/user/login");
	private static final RequestMatcher API_USER_LOGIN = PathPatternRequestMatcher.pathPattern("/api/user/login");
	private static final RequestMatcher USER_REGISTER = PathPatternRequestMatcher.pathPattern("/user/register");
	private static final RequestMatcher API_USER_REGISTER = PathPatternRequestMatcher.pathPattern("/api/user/register");

	private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oauth2LoginFailureHandler;
	private final SocialOAuth2UserService socialOAuth2UserService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf
				    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				    .ignoringRequestMatchers(
						    BEARER_TOKEN_REQUEST,
						    USER_LOGIN,
						    API_USER_LOGIN,
						    USER_REGISTER,
						    API_USER_REGISTER
				    ))
		    .httpBasic(AbstractHttpConfigurer::disable)
		    .formLogin(AbstractHttpConfigurer::disable)
		    .logout(AbstractHttpConfigurer::disable)
		    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
		    .oauth2Login(oauth2 -> oauth2
				    .userInfoEndpoint(userInfo -> userInfo.userService(socialOAuth2UserService))
				    .successHandler(oauth2LoginSuccessHandler)
				    .failureHandler(oauth2LoginFailureHandler)
		    )
		    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(
				"http://localhost:5173",
				"https://vti-clothing-shop-oozsn0fm9-restom0s-projects.vercel.app"
		));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
