package com.hardware_today.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hardware_today.dto.UserDTO;
import com.hardware_today.projections.CartProjection;
import com.hardware_today.repository.CartRepository;
import com.hardware_today.utils.JwtUtil;

@Service
public class CartService {
	private final CartRepository cartRepository;
	private final JwtUtil jwtUtil;
	
	public CartService(CartRepository cartRepository, JwtUtil jwtUtil) {
		this.cartRepository = cartRepository;
		this.jwtUtil = jwtUtil;
	}
	
	public CartProjection getCartById(UUID id) {
		return this.cartRepository.getCartById(id).orElseThrow();
	}
	
	public List<CartProjection> getUserCarts(UUID userId) {
		return this.cartRepository.getByUser(userId).orElseThrow();
	}
	
	public List<CartProjection> extractUserCartByToken(String token) {
		UserDTO userDTO = this.jwtUtil.extractUserDTOClaim(token);
		return getUserCarts(userDTO.getId());
	}
	
//	private HashMap<UUID, CartDTO> getCartDTO(List<CartProjection> carts) {
//		for (CartProjection cart : carts) {
//			
//		}
//		
//		
//		return null;
//	}
}
