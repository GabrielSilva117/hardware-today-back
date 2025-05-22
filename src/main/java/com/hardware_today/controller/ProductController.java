package com.hardware_today.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public List<Product> getAllProducts(@RequestBody(required = false) ProductFilterModel filter) {
    	try {
    		return this.productService.getAllProducts(filter);
    	} catch (Exception err) {
    		err.printStackTrace();
    		return null;
    	}
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