package com.hardware_today.service;

import com.hardware_today.dto.PurchaseOrderDTO;
import com.hardware_today.dto.PurchaseOrderItemDTO;
import com.hardware_today.dto.UserDTO;
import com.hardware_today.entity.Cart;
import com.hardware_today.entity.CartItem;
import com.hardware_today.entity.PurchaseOrder;
import com.hardware_today.entity.PurchaseOrderItem;
import com.hardware_today.repository.CartRepository;
import com.hardware_today.repository.PurchaseOrderRepository;
import com.hardware_today.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CartRepository cartRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public PurchaseOrder createFromCart(UUID cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create purchase order from an empty cart");
        }

        double total = 0;
        List<PurchaseOrderItem> lineItems = new ArrayList<>();
        Instant placedAt = Instant.now();

        for (CartItem cartItem : cart.getItems()) {
            double unitPrice = cartItem.getProduct().getPrice();
            int qty = cartItem.getQuantity() != null ? cartItem.getQuantity() : 0;
            double line = unitPrice * qty;
            total += line;

            PurchaseOrderItem poi = new PurchaseOrderItem();
            poi.setProductId(cartItem.getProduct().getId());
            poi.setProductName(cartItem.getProduct().getName());
            poi.setUnitPrice(unitPrice);
            poi.setQuantity(qty);
            lineItems.add(poi);
        }

        PurchaseOrder order = new PurchaseOrder(cart.getUser(), placedAt, total);
        for (PurchaseOrderItem poi : lineItems) {
            poi.setPurchaseOrder(order);
            order.getItems().add(poi);
        }

        return purchaseOrderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderDTO> findAllForUser(String accessToken) {
        UserDTO user = jwtUtil.extractUserDTOClaim(accessToken);
        if (user == null || user.getId() == null) {
            return List.of();
        }
        return purchaseOrderRepository.findAllByUserIdWithItems(user.getId()).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseOrderDTO> findByIdForUser(UUID orderId, String accessToken) {
        UserDTO user = jwtUtil.extractUserDTOClaim(accessToken);
        if (user == null || user.getId() == null) {
            return Optional.empty();
        }
        return purchaseOrderRepository.findByIdAndUserIdWithItems(orderId, user.getId())
                .map(this::toDto);
    }

    private PurchaseOrderDTO toDto(PurchaseOrder po) {
        List<PurchaseOrderItemDTO> itemDtos = po.getItems().stream()
                .map(i -> new PurchaseOrderItemDTO(
                        i.getProductId(),
                        i.getProductName(),
                        i.getUnitPrice(),
                        i.getQuantity(),
                        i.getUnitPrice() * i.getQuantity()
                ))
                .toList();
        return new PurchaseOrderDTO(po.getId(), po.getPlacedAt(), po.getTotalAmount(), itemDtos);
    }
}
