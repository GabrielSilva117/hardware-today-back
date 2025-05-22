package com.hardware_today.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hardware_today.entity.Category;
import com.hardware_today.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {
private final CategoryService categoryService;
	
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@GetMapping
	public ResponseEntity<List<Category>> getCategories() {
		try {
			return ResponseEntity.ok(this.categoryService.getCategories());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
