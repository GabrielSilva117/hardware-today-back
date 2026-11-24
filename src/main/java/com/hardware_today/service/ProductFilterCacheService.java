package com.hardware_today.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.hardware_today.model.ProductFilterModel;

import lombok.RequiredArgsConstructor;

/**
 * Keeps the last product filter each user applied so they don't have to resend it on every
 * request. Entries expire 5 minutes after they are created (see {@code ProductFilterCacheConfig}).
 */
@Service
@RequiredArgsConstructor
public class ProductFilterCacheService {

    private final Cache<String, ProductFilterModel> productFilterCache;

    /**
     * Resolves the filter to actually run the query with:
     * <ul>
     *   <li>if the request carries filter criteria, it is a user interaction: the cache is
     *       refreshed with it and the same filter is returned;</li>
     *   <li>otherwise the last cached filter for this user is returned, falling back to the
     *       (empty) incoming filter when there is nothing cached.</li>
     * </ul>
     */
    public ProductFilterModel resolve(ProductFilterModel incoming) {
        if (incoming == null) incoming = new ProductFilterModel();

        String userKey = currentUserKey();
        if (userKey == null) return incoming;

        if (incoming.hasCriteria()) {productFilterCache
            .put(userKey, incoming);
            return incoming;
        }

        ProductFilterModel cached = productFilterCache.getIfPresent(userKey);
        return cached != null ? cached : incoming;
    }

    private String currentUserKey() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) return userDetails.getUsername();
        if (principal == null || "anonymousUser".equals(principal)) return null;
        return principal.toString();
    }
}
