package com.hardware_today.repository.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.hardware_today.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProductCustomRepositoryImpl implements ProductCustomRepository {
	@PersistenceContext
	EntityManager entityManager;
	
	@Override
	public List<Product> getAllProducts(List<UUID> brands,  List<UUID> categories, BigDecimal minPrice, BigDecimal maxPrice) {
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Product> query = cb.createQuery(Product.class);
		
		Root<Product> product = query.from(Product.class);
		
		Predicate predicate = cb.conjunction();
		
		
		if (!brands.isEmpty()) {
			predicate = cb.and(predicate, product.get("brand").get("id").in(brands));
		}
		
		if (!categories.isEmpty()) {
			predicate = cb.and(predicate, product.get("category").get("id").in(categories));
		}
		
		predicate = cb.and(predicate, cb.greaterThanOrEqualTo(product.get("price"), minPrice));
		predicate = cb.and(predicate, cb.lessThanOrEqualTo(product.get("price"), maxPrice));
		
		query.where(predicate);
		
		return entityManager.createQuery(query).getResultList();
	}
	
}
