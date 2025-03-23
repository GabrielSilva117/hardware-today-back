package com.hardware_today.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hardware_today.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
}
