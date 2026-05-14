package com.hardware_today.controller;

import com.hardware_today.dto.CardPayment;
import com.hardware_today.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/pay/card")
    public ResponseEntity<UUID> payCart (@CookieValue(value="active_cart", required=false) UUID activeCartId,
                                         @RequestBody(required = false) CardPayment paymentData,
                                         HttpServletResponse response) {
//        this.cartService.publishMessage();
        try {
            this.paymentService.publishPayment(activeCartId, paymentData, response);
            return ResponseEntity.ok().body(activeCartId);
        } catch(Exception e) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(activeCartId);
        }

    }
}
