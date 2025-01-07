package com.hardware_today.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hardware_today.entity.ProductEntity;
import com.hardware_today.repository.Product.ProductCustomRepository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, ProductCustomRepository  {}