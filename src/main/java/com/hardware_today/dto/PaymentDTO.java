package com.hardware_today.dto;

import com.hardware_today.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentDTO {
    BigDecimal amount;
    String currency;
    PaymentType type;
    UUID cartId;
}
