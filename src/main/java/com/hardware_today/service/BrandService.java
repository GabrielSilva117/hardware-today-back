package com.hardware_today.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hardware_today.entity.Brand;
import com.hardware_today.repository.BrandRepository;

@Service
public class BrandService {
	private final BrandRepository brandRepository;
	
	public BrandService(BrandRepository brandRepository) {
		this.brandRepository = brandRepository;
	}
	
	public List<Brand> getBrands() {
		return this.brandRepository.findAll();
	}
}
