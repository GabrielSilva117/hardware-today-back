package com.hardware_today.specifications;

import com.hardware_today.entity.Brand;
import com.hardware_today.entity.Category;
import com.hardware_today.entity.Product;
import com.hardware_today.model.ProductFilterModel;
import com.hardware_today.utils.ServiceUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductSpecification {

    public static Specification<Product> searchByTerm(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isBlank()) return cb.conjunction();

            String pattern = "%" + term.toLowerCase() + "%";

            Join<Product, Brand> brand = root.join("brand", JoinType.LEFT);
            Join<Product, Category> category = root.join("category", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(brand.get("name")), pattern),
                    cb.like(cb.lower(category.get("name")), pattern)
            );
        };
    }

    public static Specification<Product> filterByBrands(List<UUID> brands) {
        return (root, query, cb) -> {
            if (brands == null || brands.isEmpty()) return cb.conjunction();
            return root.get("brand").get("id").in(brands);
        };
    }

    public static Specification<Product> filterByCategories(List<UUID> categories) {
        return (root, query, cb) -> {
            if (categories == null || categories.isEmpty()) return cb.conjunction();
            return root.get("category").get("id").in(categories);
        };
    }

    public static Specification<Product> filterByMinPrice(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    public static Specification<Product> filterByMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> build(ProductFilterModel filter) {
        return Specification
                .where(searchByTerm(filter.getTerm()))
                .and(filterByBrands(ServiceUtils.parseCommaSeparatedUUID(filter.getBrand())))
                .and(filterByCategories(ServiceUtils.parseCommaSeparatedUUID(filter.getCategory())))
                .and(filterByMinPrice(filter.getMinPrice()))
                .and(filterByMaxPrice(filter.getMaxPrice()));
    }
}