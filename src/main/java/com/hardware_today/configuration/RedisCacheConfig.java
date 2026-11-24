package com.hardware_today.configuration;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * TTL-only cache for product listing pages (see {@code ProductPageCacheService}). Entries expire
 * on their own; there is no explicit eviction on product writes, so a write can be visible in
 * search results up to {@link #PRODUCTS_TTL} late.
 */
@Configuration
public class RedisCacheConfig {

    public static final String PRODUCTS_CACHE = "products";
    private static final Duration PRODUCTS_TTL = Duration.ofMinutes(3);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(PRODUCTS_TTL)
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration(PRODUCTS_CACHE, defaultConfig)
                .build();
    }
}
