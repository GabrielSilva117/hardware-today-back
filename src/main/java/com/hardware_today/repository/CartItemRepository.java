package com.hardware_today.repository;

import com.hardware_today.entity.Cart;
import com.hardware_today.entity.CartItem;
import com.hardware_today.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    @Modifying
    @Transactional
    @Query(value = "UPDATE cart_item c SET cart_id = :newCart WHERE cart_id = :cartToMerge", nativeQuery = true)
    void changeCartsById(UUID cartToMerge, UUID newCart);
}
