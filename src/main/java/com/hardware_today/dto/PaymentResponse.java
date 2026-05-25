package com.hardware_today.dto;

import com.hardware_today.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private PaymentStatus status;
    /** Set by ecommerce after creating a purchase order; omitted when payment fails or PO creation is skipped. */
    private UUID purchaseOrderId;
}
