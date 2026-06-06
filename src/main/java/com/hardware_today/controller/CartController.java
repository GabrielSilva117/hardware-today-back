package com.hardware_today.controller;

import java.util.UUID;

import com.hardware_today.dto.CartStateActionDTO;
import com.hardware_today.dto.CartStateToggle;
import com.hardware_today.dto.UserCartsResDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<UserCartsResDTO> getCartByUser(@CookieValue(value="access_token", required=false) String accessToken) {
		try {
			return ResponseEntity.ok().body(cartService.extractUserCartByToken(accessToken));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@PostMapping("/add/{productId}")
	public ResponseEntity<String> addProductToCart(@CookieValue(value="access_token", required=false) String accessToken, 
		@PathVariable UUID productId, HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.addProductToCart(accessToken, productId, response));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

    @PostMapping("/toggle-state/{cartId}")
    public ResponseEntity<Boolean> toggleState(HttpServletResponse response,
           @CookieValue(value="access_token", required=false) String token, @PathVariable UUID cartId, @RequestBody CartStateToggle body) {
        try {
            return ResponseEntity.ok().body(cartService.changeCartState(response, token, cartId, body.getCartName()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/state-action")
    public ResponseEntity<String> runStateAction(@CookieValue(value = "access_token", required = false) String token,
                                                 @RequestBody CartStateActionDTO body, HttpServletResponse response) {
        try {
            return ResponseEntity.ok().body(cartService.handleCartConflict(token, body.getCartToChange(), body.getShouldMerge(), response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
	
	@DeleteMapping("/product/{productId}/{quantity}")
	public ResponseEntity<String> removeProductFromCart(@CookieValue(value="access_token", required=false) String token,
			@PathVariable UUID productId, @PathVariable int quantity, HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.removeProductFromCart(productId, token, quantity, response));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	
	@DeleteMapping(value = {"", "/"})
	public ResponseEntity<String> deleteActiveCart(@CookieValue(value="access_token", required=false) String token,
			HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.deleteCart(token, true, response));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	
	@DeleteMapping("/{cartId}")
	public ResponseEntity<String> deleteCartById(@CookieValue(value="access_token", required=false) UUID activeCartId, 
			@PathVariable UUID cartId, HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.deleteCart(cartId, activeCartId.equals(cartId), response));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

    @GetMapping(value = {"", "/"})
    public ResponseEntity<UUID> getActiveCartId(@CookieValue(value="access_token", required = false) UUID cartId) {
        return ResponseEntity.ok().body(cartId);
    }
}
