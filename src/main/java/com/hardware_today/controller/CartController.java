package com.hardware_today.controller;

import java.util.UUID;

import com.hardware_today.dto.CartStateActionDTO;
import com.hardware_today.dto.CartStateToggle;
import com.hardware_today.dto.UserCartsResDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hardware_today.projections.CartProjection;
import com.hardware_today.service.CartService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/cart")
public class CartController {
	private static final Logger log = LoggerFactory.getLogger(CartController.class);

	private final CartService cartService;
	
	public CartController(CartService cartService) {
		this.cartService = cartService;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CartProjection> getCartById(@PathVariable UUID id) {
		try {
			return ResponseEntity.ok().body(cartService.getCartById(id));
		} catch (Exception e) {
			log.error("Failed to fetch cart {}", id, e);
			return null;
		}
	}
	
	@GetMapping("/user")
	public ResponseEntity<UserCartsResDTO> getCartByUser(@CookieValue(value="access_token", required=false) String accessToken) {
		try {
			return ResponseEntity.ok().body(cartService.extractUserCartByToken(accessToken));
		} catch (Exception e) {
			log.error("Failed to fetch cart for user", e);
			return null;
		}
	}
	
	@PostMapping("/add/{productId}/{qtd}")
	public ResponseEntity<String> addProductToCart(@CookieValue(value="access_token", required=false) String accessToken, 
		@PathVariable UUID productId, @PathVariable int qtd, HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.addProductToCart(accessToken, productId, qtd, response));
		} catch (Exception e) {
			log.error("Failed to add product {} to cart", productId, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

    @PostMapping("/toggle-state/{cartId}")
    public ResponseEntity<Boolean> toggleState(HttpServletResponse response,
           @CookieValue(value="access_token", required=false) String token, @PathVariable UUID cartId, @RequestBody CartStateToggle body) {
        try {
            return ResponseEntity.ok().body(cartService.changeCartState(response, token, cartId, body.getCartName()));
        } catch (Exception e) {
            log.error("Failed to change state for cart {}", cartId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/state-action")
    public ResponseEntity<String> runStateAction(@CookieValue(value = "access_token", required = false) String token,
                                                 @RequestBody CartStateActionDTO body, HttpServletResponse response) {
        try {
            return ResponseEntity.ok().body(cartService.handleCartConflict(token, body.getCartToChange(), body.getShouldMerge(), response));
        } catch (Exception e) {
            log.error("Failed to resolve cart conflict for cart {}", body.getCartToChange(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
	
	@DeleteMapping("/product/{productId}/{quantity}")
	public ResponseEntity<String> removeProductFromCart(@CookieValue(value="access_token", required=false) String token,
			@PathVariable UUID productId, @PathVariable int quantity, HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.removeProductFromCart(productId, token, quantity, response));
		} catch (Exception e) {
			log.error("Failed to remove product {} from cart", productId, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	
	@DeleteMapping(value = {"", "/"})
	public ResponseEntity<String> deleteActiveCart(@CookieValue(value="access_token", required=false) String token,
			HttpServletResponse response) {
		try {
			return ResponseEntity.ok().body(cartService.deleteCart(token, true, response));
		} catch (Exception e) {
			log.error("Failed to delete active cart", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	
	@DeleteMapping("/{cartId}")
	public ResponseEntity<String> deleteCartById(@CookieValue(value="access_token", required=false) String token,
			@PathVariable UUID cartId, HttpServletResponse response) {
		try {
			UUID activeCartId = cartService.getActiveCartId(token);
			return ResponseEntity.ok().body(cartService.deleteCart(cartId, cartId.equals(activeCartId), response));
		} catch (Exception e) {
			log.error("Failed to delete cart {}", cartId, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

    @GetMapping(value = {"", "/"})
    public ResponseEntity<UUID> getActiveCartId(@CookieValue(value="access_token", required = false) String token) {
        try {
            return ResponseEntity.ok().body(cartService.getActiveCartId(token));
        } catch (Exception e) {
            log.error("Failed to fetch active cart id", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
