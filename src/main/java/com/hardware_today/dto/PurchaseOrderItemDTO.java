package com.hardware_today.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemDTO {
    private UUID productId;
    private String productName;
    private double unitPrice;
    private int quantity;
    private double lineTotal;
}
