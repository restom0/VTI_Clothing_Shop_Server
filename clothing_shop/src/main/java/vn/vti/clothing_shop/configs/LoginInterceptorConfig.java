package vn.vti.clothing_shop.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.vti.clothing_shop.middlewares.LoginInterceptor;
import vn.vti.clothing_shop.middlewares.RateLimitInterceptor;

@Configuration
@RequiredArgsConstructor
public class LoginInterceptorConfig implements WebMvcConfigurer {
    private final RateLimitInterceptor rateLimitInterceptor;
    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**")
                .order(Ordered.HIGHEST_PRECEDENCE);

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .order(Ordered.HIGHEST_PRECEDENCE + 1);
    }
}
