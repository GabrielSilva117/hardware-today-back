package com.hardware_today.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hardware_today.dto.CartDTO;
import com.hardware_today.entity.Cart;
import com.hardware_today.projections.CartProjection;
import com.hardware_today.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {
	private final CartService cartService;
	
	public CartController(CartService cartService) {
		this.cartService = cartService;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CartProjection> getCartById(@PathVariable UUID id) {
		try {
			return ResponseEntity.ok().body(cartService.getCartById(id));	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@GetMapping("/user")
	public ResponseEntity<List<CartDTO>> getCartByUser(@CookieValue(value="access_token", required=false) String accessToken) {
		try {
			return ResponseEntity.ok().body(cartService.extractUserCartByToken(accessToken));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@PostMapping("/add/{productId}")
	public ResponseEntity<String> addProductToCart(@CookieValue(value="access_token", required=false) String accessToken, @PathVariable UUID productId) {
		try {
			return ResponseEntity.ok().body(cartService.addProductToCart(accessToken, productId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
