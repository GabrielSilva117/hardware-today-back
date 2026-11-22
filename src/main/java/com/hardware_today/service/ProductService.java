package com.hardware_today.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.hardware_today.model.CachedProductPage;
import com.hardware_today.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hardware_today.entity.Product;
import com.hardware_today.model.ProductFilterModel;
import com.hardware_today.repository.ProductRepository;
import com.hardware_today.utils.ServiceUtils;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private final ProductFilterCacheService productFilterCacheService;
    private final ProductPageCacheService productPageCacheService;

	public ProductService (
			ProductRepository productRepository,
			ProductFilterCacheService productFilterCacheService,
			ProductPageCacheService productPageCacheService
	) {
		this.productRepository = productRepository;
		this.productFilterCacheService = productFilterCacheService;
		this.productPageCacheService = productPageCacheService;
	}
	
//	public List<Product> getAllProducts(ProductFilterModel filter) {
//        if (filter != null) {
//            return productRepository.getAllProducts(
//            		ServiceUtils.parseCommaSeparatedUUID(filter.getBrand()),
//            		ServiceUtils.parseCommaSeparatedUUID(filter.getCategory()),
//                    filter.getMinPrice(),
//                    filter.getMaxPrice()
//            );
//        }
//
//        return productRepository.findAll();
//    }

    public Page<ProductProjection> getAllProducts(ProductFilterModel filter, Pageable pageable) {
//        filter = productFilterCacheService.resolve(filter);

        CachedProductPage cached = productPageCacheService.getPage(filter, pageable);
        List<ProductProjection> content = new ArrayList<>(cached.getContent());
        return new PageImpl<>(content, pageable, cached.getTotalElements());
    }
	
	public Product getProductById(UUID id) {
		return this.productRepository.findById(id).orElseThrow();
	}
}
