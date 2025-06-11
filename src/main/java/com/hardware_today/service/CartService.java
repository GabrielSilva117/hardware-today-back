package com.hardware_today.service;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardware_today.dto.CartDTO;
import com.hardware_today.dto.UserDTO;
import com.hardware_today.entity.Cart;
import com.hardware_today.entity.Product;
import com.hardware_today.projections.CartProjection;
import com.hardware_today.projections.ProductProjection;
import com.hardware_today.repository.CartRepository;
import com.hardware_today.repository.ProductRepository;
import com.hardware_today.utils.CookieHandler;
import com.hardware_today.utils.JwtUtil;

@Service
public class CartService {
	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final JwtUtil jwtUtil;
	
	public CartService(CartRepository cartRepository, JwtUtil jwtUtil, ProductRepository productRepository) {
		this.cartRepository = cartRepository;
		this.jwtUtil = jwtUtil;
		this.productRepository = productRepository;
	}
	
	public CartProjection getCartById(UUID id) {
		return this.cartRepository.getCartById(id).orElseThrow();
	}
	
	public List<CartProjection> getUserCarts(UUID userId) {
		return this.cartRepository.getByUser(userId).orElseThrow();
	}
	
	public List<CartDTO> extractUserCartByToken(String token) {
		UserDTO userDTO = this.jwtUtil.extractUserDTOClaim(token);
		return this.getCartDTO(this.getUserCarts(userDTO.getId()));
	}
	
	@Transactional
	public String addProductToCart(String token, UUID productId, UUID cartId, HttpServletResponse response) {
		UserDTO userDTO = this.jwtUtil.extractUserDTOClaim(token);
		Cart cart = new Cart();
		
		if (cartId != null) cart = cartRepository.findById(cartId).orElse(cart);
		
		Product product = productRepository.findById(productId).orElseThrow();
		
		if (cart.getUser() == null) cart.setUser(entityManager.getReference(User.class, userDTO.getId()));
		
		
		cart.getProducts().add(product);
		cartRepository.save(cart);
		
		if (cartId == null || !cartId.equals(cart.getId())) CookieHandler.addCookie(cart.getId().toString(), "active_cart", 604800, response);
		
		return "Product added successfully";
	}
	
	private List<CartDTO> getCartDTO(List<CartProjection> carts) {
		List<CartDTO> cartDTOList = new ArrayList<CartDTO>();
		
		
		if (!carts.isEmpty()) {
			 for (CartProjection cart : carts) {
				 CartDTO cartDTO = new CartDTO(cart.getId(), cart.getEnabled(), cart.getProducts(), 0.0);
				 cartDTO.setTotalPrice(cart.getProducts().stream().mapToDouble(ProductProjection::getPrice).sum());				 
				 cartDTOList.add(cartDTO);
			 }
		}
		
		
		return cartDTOList;
	}
}
