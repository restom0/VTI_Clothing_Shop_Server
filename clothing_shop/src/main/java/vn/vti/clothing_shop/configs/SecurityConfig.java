package vn.vti.clothing_shop.configs;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
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
	private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oauth2LoginFailureHandler;
	private final SocialOAuth2UserService socialOAuth2UserService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
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
