package vn.vti.clothing_shop.configs;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.UrlHandlerFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import vn.vti.clothing_shop.middlewares.LoginInterceptor;
import vn.vti.clothing_shop.middlewares.RateLimitInterceptor;
import vn.vti.clothing_shop.middlewares.TimeIntervalInterceptor;

@Configuration
@RequiredArgsConstructor
public class LoginInterceptorConfig implements WebMvcConfigurer {
	private final RateLimitInterceptor rateLimitInterceptor;
	private final TimeIntervalInterceptor timeIntervalInterceptor;
	private final LoginInterceptor loginInterceptor;

	/** Handles trailing slash handler filter. */
	@Bean
	public FilterRegistrationBean<UrlHandlerFilter> trailingSlashHandlerFilter() {
		UrlHandlerFilter filter = UrlHandlerFilter
				.trailingSlashHandler("/**")
				.wrapRequest()
				.build();
		FilterRegistrationBean<UrlHandlerFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registration;
	}

	/** Adds interceptors. */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(rateLimitInterceptor)
		        .addPathPatterns("/**")
		        .order(Ordered.HIGHEST_PRECEDENCE);

		registry.addInterceptor(timeIntervalInterceptor)
		        .addPathPatterns("/**")
		        .order(Ordered.HIGHEST_PRECEDENCE + 1);

		registry.addInterceptor(loginInterceptor)
		        .addPathPatterns("/**")
		        .order(Ordered.HIGHEST_PRECEDENCE + 2);
	}
}
