package com.hardware_today.controller;

import java.util.List;
import java.util.UUID;

import com.hardware_today.dto.CartStateActionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.hardware_today.dto.CartDTO;
import com.hardware_today.entity.Cart;
import com.hardware_today.projections.CartProjection;
import com.hardware_today.service.CartService;

import jakarta.servlet.http.HttpServletResponse;

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
	public ResponseEntity<String> addProductToCart(@CookieValue(value="access_token", required=false) String accessToken, 
			@CookieValue(value="active_cart", required=false) UUID cartId, @PathVariable UUID productId, HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.addProductToCart(accessToken, productId, cartId, response));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

    @PostMapping("/toggle-state/{cartId}")
    public ResponseEntity<Boolean> toggleState(@CookieValue(value="access_token", required=false) String accessToken,
           @CookieValue(value="active_cart", required=false) UUID activeCartId, @PathVariable UUID cartId) {
        try {
            return ResponseEntity.ok().body(cartService.changeCartState(accessToken, activeCartId, cartId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/state-action")
    public ResponseEntity<String> runStateAction(@CookieValue(value = "active_cart", required = false) UUID activeCardId,
                                                 @RequestBody CartStateActionDTO body, HttpServletResponse response) {
        try {
            return ResponseEntity.ok().body(cartService.handleCartConflict(activeCardId, body.getCartToChange(), body.getShouldMerge(), response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
	
	@DeleteMapping("/product/{productId}/{quantity}")
	public ResponseEntity<String> removeProductFromCart(@CookieValue(value="active_cart", required=false) UUID cartId,
			@PathVariable UUID productId, @PathVariable int quantity, HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.removeProductFromCart(productId, cartId, quantity, response));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	
	@DeleteMapping("/")
	public ResponseEntity<String> deleteActiveCart(@CookieValue(value="active_cart", required=false) UUID cartId,
			HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.deleteCart(cartId, true, response));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	
	@DeleteMapping("/{cartId}")
	public ResponseEntity<String> deleteCartById(@CookieValue(value="active_cart", required=false) UUID activeCartId, 
			@PathVariable UUID cartId, HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.deleteCart(cartId, activeCartId.equals(cartId), response));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
