package com.hardware_today.controller;

import com.hardware_today.entity.ProductEntity;
import com.hardware_today.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }
}