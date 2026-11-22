package com.hardware_today.utils;

import org.springframework.data.domain.Pageable;

import com.hardware_today.model.ProductFilterModel;

/**
 * Builds the Redis key for a cached product page. {@code ProductFilterModel} has no
 * {@code equals}/{@code hashCode}, so Spring's default key generator can't be used here — the
 * key is built explicitly from the fields that actually affect the query result.
 */
public final class ProductCacheKeyGenerator {

    private ProductCacheKeyGenerator() {}

    public static String build(ProductFilterModel filter, Pageable pageable) {
        if (filter == null) filter = new ProductFilterModel();

        return String.join("|",
                "term=" + filter.getTerm(),
                "brand=" + filter.getBrand(),
                "category=" + filter.getCategory(),
                "minPrice=" + filter.getMinPrice(),
                "maxPrice=" + filter.getMaxPrice(),
                "page=" + pageable.getPageNumber(),
                "size=" + pageable.getPageSize(),
                "sort=" + pageable.getSort()
        );
    }
}
