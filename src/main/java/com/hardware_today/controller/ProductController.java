package com.hardware_today.controller;

import java.util.List;
import java.util.UUID;

import com.hardware_today.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hardware_today.entity.Product;
import com.hardware_today.model.ProductFilterModel;
import com.hardware_today.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
	private ProductService productService;
	
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

//    public List<Product> getAllProducts(@RequestBody(required = false) ProductFilterModel filter) {
//    	try {
//    		return this.productService.getAllProducts(filter);
//    	} catch (Exception err) {
//    		err.printStackTrace();
//    		return null;
//    	}
//    }
    @PostMapping
    public ResponseEntity<Page<ProductProjection>> getAllProducts(
            @RequestBody ProductFilterModel filter,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProducts(filter, pageable));
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable UUID productId) {
    	try {
    		return this.productService.getProductById(productId);
    	} catch (Exception err) {
    		err.printStackTrace();
    		return null;
    	}
    }
}