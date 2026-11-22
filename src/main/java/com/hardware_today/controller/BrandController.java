package com.hardware_today.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hardware_today.entity.Brand;
import com.hardware_today.service.BrandService;

@RestController
@RequestMapping("/brand")
public class BrandController {
	private static final Logger log = LoggerFactory.getLogger(BrandController.class);

	private final BrandService brandService;
	
	public BrandController(BrandService brandService) {
		this.brandService = brandService;
	}
	
	@GetMapping
	public ResponseEntity<List<Brand>> getBrands() {
		try {
			return ResponseEntity.ok(this.brandService.getBrands());
		} catch (Exception e) {
			log.error("Failed to fetch brands", e);
			return null;
		}
	}
}
