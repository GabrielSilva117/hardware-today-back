package com.hardware_today.service;

import java.util.List;
import java.util.UUID;

import com.hardware_today.projections.ProductProjection;
import com.hardware_today.specifications.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hardware_today.entity.Product;
import com.hardware_today.model.ProductFilterModel;
import com.hardware_today.repository.ProductRepository;
import com.hardware_today.utils.ServiceUtils;

@Service
public class ProductService {
    private ProductRepository productRepository;
	
	public ProductService (ProductRepository productRepository) {
		this.productRepository = productRepository;
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
        if (filter == null) filter = new ProductFilterModel();

        Specification<Product> spec = ProductSpecification.build(filter);
        return productRepository.findBy(spec, q -> q.as(ProductProjection.class).page(pageable));
    }
	
	public Product getProductById(UUID id) {
		return this.productRepository.findById(id).orElseThrow();
	}
}
