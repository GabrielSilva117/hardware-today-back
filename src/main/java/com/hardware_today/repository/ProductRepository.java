package com.hardware_today.repository;

import com.hardware_today.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query(value = "SELECT p FROM product p where (:brand IS NULL OR p.brand.id IN (:brands))" +
            " AND (:category IS NULL OR p.category.id IN (:categories)) " +
            "AND p.price BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 9999)")
    List<ProductEntity> findAllFilteredValues(@Param("brands") String brands, @Param("categories") String categories,
                                            @Param("minPrice") String minPrice, @Param("maxPrice") String maxPrice);
}