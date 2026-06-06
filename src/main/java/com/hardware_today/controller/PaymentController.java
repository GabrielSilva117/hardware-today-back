package com.hardware_today.controller;

import com.hardware_today.dto.CardPayment;
import com.hardware_today.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/pay/card")
    public ResponseEntity<String> payCart (@CookieValue(value="access_token", required=true) String token,
                                         @RequestBody(required = false) CardPayment paymentData,
                                         HttpServletResponse response) {
        try {
            this.paymentService.publishPayment(token, paymentData, response);
            return ResponseEntity.ok().body("Payment completed!");
        } catch(Exception e) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
