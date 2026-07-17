package vn.vti.clothing_shop.configs;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {
	private final ObjectMapper objectMapper;

	@Value("${spring.data.redis.host:localhost}")
	private String redisHost;

	@Value("${spring.data.redis.port:6379}")
	private int redisPort;

	@Value("${spring.data.redis.username:}")
	private String redisUsername;

	@Value("${spring.data.redis.password:}")
	private String redisPassword;

	/** Handles cache configuration. */
	@Bean
	public RedisCacheConfiguration cacheConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
		                              .entryTtl(Duration.ofMinutes(60))
		                              .disableCachingNullValues()
		                              .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
				                              new GenericJackson2JsonRedisSerializer(objectMapper)));
	}

	/** Handles jedis connection factory. */
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(redisHost);
		config.setPort(redisPort);
		if (StringUtils.hasText(redisUsername)) {
			config.setUsername(redisUsername);
		}
		if (StringUtils.hasText(redisPassword)) {
			config.setPassword(redisPassword);
		}
		return new JedisConnectionFactory(config);
	}

	/** Handles redis util redis template. */
	@Bean(name = "customRedisTemplate")
	public RedisTemplate<Object, Object> redisUtilRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setDefaultSerializer(new CustomRedisSerializer());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new CustomRedisSerializer());
		redisTemplate.setHashValueSerializer(new CustomRedisSerializer());
		return redisTemplate;
	}

	/** Handles redis template. */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}
}
