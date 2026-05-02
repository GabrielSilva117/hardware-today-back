package com.hardware_today.controller;

import com.hardware_today.dto.PurchaseOrderDTO;
import com.hardware_today.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping(value = {"", "/"})
    public ResponseEntity<List<PurchaseOrderDTO>> listMine(
            @CookieValue(value = "access_token", required = false) String accessToken) {
        return ResponseEntity.ok(purchaseOrderService.findAllForUser(accessToken));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> getMine(
            @PathVariable UUID id,
            @CookieValue(value = "access_token", required = false) String accessToken) {
        return purchaseOrderService.findByIdForUser(id, accessToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
