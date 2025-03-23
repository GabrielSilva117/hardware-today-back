package com.hardware_today.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hardware_today.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
