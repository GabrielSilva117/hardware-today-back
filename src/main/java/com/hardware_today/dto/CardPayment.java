package com.hardware_today.dto;

import com.hardware_today.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
public class CardPayment extends PaymentDTO {
    private String number;
    private String cvv;
    private String cardHolderName;
    private String expirationDate;

    public CardPayment(
            BigDecimal amount,
            String currency,
            PaymentType type,
            UUID cartId,
            String number,
            String cvv,
            String cardHolderName,
            String expirationDate) {
        super(amount, currency, type, cartId);
        this.number = number;
        this.cvv = cvv;
        this.cardHolderName = cardHolderName;
        this.expirationDate = expirationDate;
    }
}
