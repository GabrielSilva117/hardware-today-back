package com.hardware_today.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hardware_today.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, UUID> {

}
