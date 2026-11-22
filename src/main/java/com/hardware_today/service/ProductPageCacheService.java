package com.hardware_today.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hardware_today.configuration.RedisCacheConfig;
import com.hardware_today.entity.Product;
import com.hardware_today.model.CachedProductPage;
import com.hardware_today.model.ProductFilterModel;
import com.hardware_today.projections.ProductCacheDto;
import com.hardware_today.projections.ProductProjection;
import com.hardware_today.repository.ProductRepository;
import com.hardware_today.specifications.ProductSpecification;
import com.hardware_today.utils.ImageUrlResolver;

import lombok.RequiredArgsConstructor;

/**
 * Caches product listing pages in Redis, keyed by filter + page + size (see
 * {@code ProductCacheKeyGenerator}). TTL-only invalidation: entries just expire (see
 * {@code RedisCacheConfig}), there is no eviction on product writes, so a write can take up to
 * the TTL to show up in listings.
 */
@Service
@RequiredArgsConstructor
public class ProductPageCacheService {

    private final ProductRepository productRepository;
    private final ImageUrlResolver imageUrlResolver;

    @Cacheable(
            value = RedisCacheConfig.PRODUCTS_CACHE,
            key = "T(com.hardware_today.utils.ProductCacheKeyGenerator).build(#filter, #pageable)"
    )
    public CachedProductPage getPage(ProductFilterModel filter, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.build(filter);
        Page<ProductProjection> page = productRepository.findBy(spec, q -> q.as(ProductProjection.class).page(pageable));

        List<ProductCacheDto> content = page.getContent().stream()
                .map(p -> ProductCacheDto.from(p, imageUrlResolver))
                .toList();

        return new CachedProductPage(content, page.getTotalElements());
    }
}
