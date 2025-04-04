package com.hardware_today.repository.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.hardware_today.entity.Product;

public interface ProductCustomRepository {
	public List<Product> getAllProducts(List<UUID> brands,  List<UUID> categories, BigDecimal minPrice, BigDecimal maxPrice); 
}
