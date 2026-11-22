package com.hardware_today.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.hardware_today.model.ProductFilterModel;

@Configuration
public class ProductFilterCacheConfig {

    /** How long a cached filter lives, counted from the moment it is first stored. */
    public static final long TTL_NANOS = TimeUnit.MINUTES.toNanos(5);

    /**
     * Per-user store of the last product filter the user applied. The entry expires 5 minutes
     * after it is created; updating it (on further filter interactions) refreshes the data but
     * does not push the expiration further out.
     */
    @Bean
    public Cache<String, ProductFilterModel> productFilterCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfter(new CreationAnchoredExpiry())
                .build();
    }

    private static final class CreationAnchoredExpiry implements Expiry<String, ProductFilterModel> {
        @Override
        public long expireAfterCreate(String key, ProductFilterModel value, long currentTime) {
            return TTL_NANOS;
        }

        @Override
        public long expireAfterUpdate(String key, ProductFilterModel value, long currentTime, long currentDuration) {
            return currentDuration;
        }

        @Override
        public long expireAfterRead(String key, ProductFilterModel value, long currentTime, long currentDuration) {
            return currentDuration;
        }
    }
}
