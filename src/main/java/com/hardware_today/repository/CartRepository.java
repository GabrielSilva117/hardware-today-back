package com.hardware_today.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hardware_today.entity.Cart;
import com.hardware_today.projections.CartProjection;

public interface CartRepository extends JpaRepository<Cart, UUID> {
	
	@Query("SELECT c FROM Cart c JOIN c.products p WHERE c.user.id = :user ORDER BY p.category.name")
	Optional<List<CartProjection>> getByUser(@Param("user") UUID user);
	
	@Query("SELECT c FROM Cart c JOIN c.products p WHERE c.id = :id ORDER BY p.category.name")
	Optional<CartProjection> getCartById(@Param("id") UUID id);
	
}
