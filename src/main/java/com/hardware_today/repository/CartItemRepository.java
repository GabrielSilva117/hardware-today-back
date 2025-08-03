package com.hardware_today.repository;

import com.hardware_today.entity.Cart;
import com.hardware_today.entity.CartItem;
import com.hardware_today.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
