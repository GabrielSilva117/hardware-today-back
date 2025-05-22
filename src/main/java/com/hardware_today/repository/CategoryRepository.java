package com.hardware_today.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hardware_today.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
}
