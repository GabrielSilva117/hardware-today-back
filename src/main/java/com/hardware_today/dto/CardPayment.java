package com.hardware_today.dto;

import com.hardware_today.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
public class CardPayment extends PaymentDTO {
    private String paymentMethodId;

    public CardPayment(
            BigDecimal amount,
            String currency,
            PaymentType type,
            UUID cartId,
            String paymentMethodId) {
        super(amount, currency, type, cartId);
        this.paymentMethodId = paymentMethodId;
    }
}
