package com.hardware_today.service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.hardware_today.dto.*;
import com.hardware_today.entity.CartItem;
import com.hardware_today.enums.PaymentType;
import com.hardware_today.publishers.NotificationPublisher;
import com.hardware_today.publishers.PaymentPublisher;
import com.hardware_today.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardware_today.entity.Cart;
import com.hardware_today.entity.Product;
import com.hardware_today.entity.User;
import com.hardware_today.projections.CartProjection;
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
    private final NotificationPublisher notificationPublisher;

    public void publishMessage () {
        this.notificationPublisher.publishNotification(new EmailDispatchDTO(
                "Your purchase has been completed",
                "Thank you for your purchase! You can follow the delivery step using the link from bellow. Best regards from our team, Hardware Today Corp.",
                "gabriel.f.silva117@gmail.com"
        ));
    }

	public CartProjection getCartById(UUID id) {
		return this.cartRepository.getCartById(id).orElseThrow();
	}

	public List<CartProjection> getUserCarts(UUID userId) {
		return this.cartRepository.getByUser(userId).orElseThrow();
	}

	public Optional<CartProjection> getActiveCartByUser(UUID userId) {
		return this.cartRepository.getActiveCartByUser(userId);
	}

	public UserCartsResDTO extractUserCartByToken(String token) {
		UserDTO userDTO = this.jwtUtil.extractUserDTOClaim(token);
		return this.getUserCartsDTO(this.getUserCarts(userDTO.getId()));
	}

	private void clearActiveCartCookie(HttpServletResponse response) {
		CookieHandler.clearCookie("active_cart", response);
	}

    private void addCartToCookie(UUID cart, HttpServletResponse response) {
        CookieHandler.addCookie(cart.toString(), "active_cart", 604800, response);
    }

	private UserCartsResDTO getUserCartsDTO(List<CartProjection> carts) {
		List<CartDTO> cartDTOList = new ArrayList<>();
        CartDTO activeCart = new CartDTO();

		if (!carts.isEmpty()) {
			 for (CartProjection cart : carts) {
				 CartDTO cartDTO = new CartDTO(cart.getId(), cart.getEnabled(), cart.getItems(), 0.0, cart.getName());
                 cartDTO.setTotalPrice(cart.getItems().stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum());

                 if (cartDTO.isEnabled()) {
                     activeCart = cartDTO;
                     continue;
                 }

				 cartDTOList.add(cartDTO);
			 }
		}


		return new UserCartsResDTO(activeCart, cartDTOList);
	}

    @Transactional
    public String deleteCart(String token, boolean isActiveCart, HttpServletResponse response) throws Exception {
        UUID cartId = this.getActiveCartId(token);
        return this.deleteCart(cartId, isActiveCart, response);
    }

    @Transactional
    public String deleteCart(UUID cartId, boolean isActiveCart, HttpServletResponse response) throws Exception {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        cartRepository.delete(cart);
        if (isActiveCart) clearActiveCartCookie(response);
        return "Cart deleted successfully!";
    }


    @Transactional
    public String removeProductFromCart(UUID productId, String token, Integer quantity, HttpServletResponse response) throws Exception {
        UUID cartId = this.getActiveCartId(token);
        return this.removeProductFromCart(productId, cartId, quantity, response);
    }

    @Transactional
    public String removeProductFromCart(UUID productId, UUID cartId, Integer quantity, HttpServletResponse response) throws Exception {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        CartItem item = cartItemRepository.findByCartAndProduct(cart, product).orElseThrow();

        if (item.getQuantity() == null) {
            item.setQuantity(1);
        }
        if (quantity != null) {
            item.setQuantity(item.getQuantity() - quantity);
        }

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
                        int q = entity.getQuantity() != null ? entity.getQuantity() : 1;
                        entity.setQuantity(q + 1);
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
    public String addProductToCart(String token, UUID productId, HttpServletResponse response) throws Exception {
        UUID cartId = this.getActiveCartId(token);

        UserDTO userDTO = this.jwtUtil.extractUserDTOClaim(token);
        Cart cart = new Cart();

        if (cartId != null) cart = cartRepository.findById(cartId).orElse(cart);

        Product product = productRepository.findById(productId).orElseThrow();

        if (cart.getUser() == null) cart.setUser(entityManager.getReference(User.class, userDTO.getId()));

        cartRepository.save(cart);
        this.addCartItem(cart, product);

        if (cartId == null || !cartId.equals(cart.getId())) addCartToCookie(cart.getId(), response);

        return product.getName();
    }

    public String handleCartConflict(String token, UUID cartId, Boolean shouldMerge, HttpServletResponse response) throws Exception {
        UUID activeCart = this.getActiveCartId(token);

        if (shouldMerge) {
            this.mergeCarts(activeCart, cartId);
            return "Cart merged successfully!";
        }

        this.swapActiveCart(activeCart, cartId, response);
        return "Cart swapped successfully";
    }

    @Transactional
    public void mergeCarts(UUID activeCart, UUID cartId) {
        cartItemRepository.changeCartsById(cartId, activeCart);
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void swapActiveCart(String token, UUID cartId, HttpServletResponse response) throws Exception {
        UUID activeCartId = this.getActiveCartId(token);
        this.swapActiveCart(activeCartId, cartId, response);
    }

    @Transactional
    public void swapActiveCart(UUID activeCart, UUID cartId, HttpServletResponse response) {
        this.toggleCartState(activeCart, "Swapped Cart");
        this.toggleCartState(cartId);
        addCartToCookie(cartId, response);
    }

    public Boolean changeCartState(HttpServletResponse response, String token, UUID cartId, String cartName) throws Exception {
        UUID activeCart = this.getActiveCartId(token);
        Cart cart = this.cartRepository.findById(cartId).orElseThrow();
        
        // If the user has and active cart and is different from the target, which is is not enabled returns false (opens confirmation pop up)
        if (activeCart != null && !activeCart.toString().isBlank() && !cartId.equals(activeCart) && !cart.isEnabled()) return false;

        this.toggleCartState(cart, cartName);

        return true;
    }

    @Transactional
    public Cart toggleCartState(Cart cart) {
        cart.setEnabled(!cart.isEnabled());
        return this.cartRepository.save(cart);
    }

    @Transactional
    public void toggleCartState(UUID cartId) {
        Cart cart = this.cartRepository.findById(cartId).orElseThrow();
        this.toggleCartState(cart);
    }

    @Transactional
    public void toggleCartState(UUID cartId, String cartName) {
        Cart cart = this.cartRepository.findById(cartId).orElseThrow();
        this.toggleCartState(cart,cartName);
    }


    @Transactional
    public Cart toggleCartState(Cart cart, String cartName) {
        cart.setEnabled(!cart.isEnabled());
        cart.setName(cartName);
        return this.cartRepository.save(cart);
    }

    public Boolean hasActiveCart(String token) {
        UserDTO userDTO = this.jwtUtil.extractUserDTOClaim(token);
        return this.cartRepository.countEnabledCartsByUser(userDTO.getId()) > 0;
    }

    public UUID getActiveCartId(String token) throws Exception{
        UUID userId = this.jwtUtil.extractUserDTOClaim(token).getId();
        Optional<CartProjection> cart = this.getActiveCartByUser(userId);
        if (cart.isEmpty()) return null;//throw new Exception("There's no active cart for this user");

        return cart.get().getId();
    }
}
