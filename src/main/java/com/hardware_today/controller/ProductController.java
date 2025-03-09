package com.hardware_today.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hardware_today.entity.ProductEntity;
import com.hardware_today.model.ProductFilterModel;
import com.hardware_today.repository.ProductRepository;

import com.hardware_today.utils.ServiceUtils;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public List<ProductEntity> getAllProducts(@RequestBody(required = false) ProductFilterModel filter) {
        if (filter != null) {
            return productRepository.getAllProducts(
            		ServiceUtils.parseCommaSeparatedUUID(filter.getBrand()), 
            		ServiceUtils.parseCommaSeparatedUUID(filter.getCategory()),
                    filter.getMinPrice(), 
                    filter.getMaxPrice()
            );
        }

        return productRepository.findAll();
    }
}