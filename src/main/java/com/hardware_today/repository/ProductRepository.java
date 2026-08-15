package com.hardware_today.repository;

import java.util.UUID;
import java.util.function.Function;

import com.hardware_today.projections.ProductProjection;
import com.hardware_today.specifications.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import com.hardware_today.entity.Product;
import com.hardware_today.repository.Product.ProductCustomRepository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>,
        JpaSpecificationExecutor<Product> {

//    <T> Page<T> findBy(Specification<Product> spec, Function<FluentQuery.FetchableFluentQuery<Product>, Page<T>> queryFunction);
}