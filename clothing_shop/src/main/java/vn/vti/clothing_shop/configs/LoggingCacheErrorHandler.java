package vn.vti.clothing_shop.configs;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * Degrades to the underlying data source when the cache server is unreachable.
 *
 * <p>Without this handler a Valkey/Redis outage turns every {@code @Cacheable} read into a
 * 500, even though PostgreSQL and MongoDB are healthy. Cache access is an optimisation,
 * so failures are logged and swallowed instead of propagated.
 */
@Slf4j
public class LoggingCacheErrorHandler implements CacheErrorHandler {
	/** Handles cache get error. */
	@Override
	public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
		log.warn("Cache read failed, falling back to the data source [cache={}, key={}]: {}",
		         cache.getName(), key, exception.getMessage());
	}

	/** Handles cache put error. */
	@Override
	public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
		log.warn("Cache write failed, value not cached [cache={}, key={}]: {}",
		         cache.getName(), key, exception.getMessage());
	}

	/**
	 * Handles cache evict error.
	 *
	 * <p>Swallowed so writes keep succeeding while the cache is down. Entries that could not be
	 * evicted expire on their own TTL.
	 */
	@Override
	public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
		log.warn("Cache evict failed, entry may be stale until it expires [cache={}, key={}]: {}",
		         cache.getName(), key, exception.getMessage());
	}

	/** Handles cache clear error. */
	@Override
	public void handleCacheClearError(RuntimeException exception, Cache cache) {
		log.warn("Cache clear failed, entries may be stale until they expire [cache={}]: {}",
		         cache.getName(), exception.getMessage());
	}
}
