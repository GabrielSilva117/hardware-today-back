package com.hardware_today.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hardware_today.entity.Category;
import com.hardware_today.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	
	public CategoryService (CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	public List<Category> getCategories() {
		return this.categoryRepository.findAll();
	}
}
