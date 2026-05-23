package com.hardware_today.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hardware_today.projections.CartProjection;
import com.hardware_today.repository.CartRepository;
import com.hardware_today.utils.CookieHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartCookieService {
    private final CartRepository cartRepository;

    public void addCartCookieByUserId(UUID userId, HttpServletResponse response) {
    	getUserCart(userId).ifPresent(cartProjection -> CookieHandler.addCookie(cartProjection.getId().toString(), "active_cart", 604800, response));
    }
    
    public Optional<CartProjection> getUserCart(UUID userId) {
    	return this.cartRepository.getActiveCartByUser(userId);
    }


    public void addCartCookieById (UUID cartId,  HttpServletResponse response) {
        CookieHandler.addCookie(cartId.toString(), "active_cart", 604800, response);
    }
    
    public void removeCookie(HttpServletResponse response) {
        CookieHandler.clearCookie("active_cart", response);
    }
}
