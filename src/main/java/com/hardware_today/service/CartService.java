package com.hardware_today.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.hardware_today.entity.CartItem;
import com.hardware_today.projections.CartItemProjection;
import com.hardware_today.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardware_today.dto.CartDTO;
import com.hardware_today.dto.UserDTO;
import com.hardware_today.entity.Cart;
import com.hardware_today.entity.Product;
import com.hardware_today.entity.User;
import com.hardware_today.projections.CartProjection;
import com.hardware_today.projections.ProductProjection;
import com.hardware_today.repository.CartRepository;
import com.hardware_today.repository.ProductRepository;
import com.hardware_today.utils.CookieHandler;
import com.hardware_today.utils.JwtUtil;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final EntityManager entityManager;
	private final JwtUtil jwtUtil;
	
	public CartProjection getCartById(UUID id) {
		return this.cartRepository.getCartById(id).orElseThrow();
	}
	
	public List<CartProjection> getUserCarts(UUID userId) {
		return this.cartRepository.getByUser(userId).orElseThrow();
	}
	
	public Optional<CartProjection> getActiveCartByUser(UUID userId) {
		return this.cartRepository.getActiveCartByUser(userId);
	}
	
	public List<CartDTO> extractUserCartByToken(String token) {
		UserDTO userDTO = this.jwtUtil.extractUserDTOClaim(token);
		return this.getCartDTO(this.getUserCarts(userDTO.getId()));
	}
	
	private void clearActiveCartCookie(HttpServletResponse response) {
		CookieHandler.clearCookie("active_cart", response);
	}
	
	@Transactional
	public String deleteCart(UUID cartId, boolean isActiveCart, HttpServletResponse response) {
		Cart cart = cartRepository.findById(cartId).orElseThrow();
		cartRepository.delete(cart);
		if (isActiveCart) clearActiveCartCookie(response);
		return "Cart deleted successfully!";
	}
	
	@Transactional
	public String removeProductFromCart(UUID productId, UUID cartId, Integer quantity, HttpServletResponse response) {
		Cart cart = cartRepository.findById(cartId).orElseThrow();
		Product product = productRepository.findById(productId).orElseThrow();
		CartItem item = cartItemRepository.findByCartAndProduct(cart, product).orElseThrow();

		if (quantity != null) item.setQuantity(item.getQuantity() - quantity);

		if (item.getQuantity() == 0) cart.getItems().remove(item);
		else {
			cartItemRepository.save(item);
			cart.getItems().add(item);
		}
		
		if (cart.getItems().isEmpty()) {
			cartRepository.delete(cart);
			clearActiveCartCookie(response);
			return null;
		}
		
		cartRepository.save(cart);
		
		return "Product removed successfully";
	}

	@Transactional
	public void addCartItem(Cart cart, Product product) {
		try {
			CartItem newItem = cartItemRepository
					.findByCartAndProduct(cart, product)
					.map(entity -> {
						entity.setQuantity(entity.getQuantity() + 1);
						return cartItemRepository.save(entity);
					})
					.orElseGet(() -> cartItemRepository.save(new CartItem(cart, product)));

			cart.getItems().add(newItem);
			cartRepository.save(cart);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Transactional
	public String addProductToCart(String token, UUID productId, UUID cartId, HttpServletResponse response) {
		UserDTO userDTO = this.jwtUtil.extractUserDTOClaim(token);
		Cart cart = new Cart();
		
		if (cartId != null) cart = cartRepository.findById(cartId).orElse(cart);
		
		Product product = productRepository.findById(productId).orElseThrow();
		
		if (cart.getUser() == null) cart.setUser(entityManager.getReference(User.class, userDTO.getId()));
		
		cartRepository.save(cart);
		this.addCartItem(cart, product);

		if (cartId == null || !cartId.equals(cart.getId())) CookieHandler.addCookie(cart.getId().toString(), "active_cart", 604800, response);
		
		return "Product added successfully";
	}
	
	private List<CartDTO> getCartDTO(List<CartProjection> carts) {
		List<CartDTO> cartDTOList = new ArrayList<CartDTO>();
		
		
		if (!carts.isEmpty()) {
			 for (CartProjection cart : carts) {
				 CartDTO cartDTO = new CartDTO(cart.getId(), cart.getEnabled(), cart.getItems(), 0.0);
				 cartDTO.setTotalPrice(cart.getItems().stream().mapToDouble(item -> item.getProduct().getPrice()).sum());
				 cartDTOList.add(cartDTO);
			 }
		}
		
		
		return cartDTOList;
	}
}
