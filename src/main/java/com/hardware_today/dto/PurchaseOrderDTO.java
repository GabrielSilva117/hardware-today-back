package com.hardware_today.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {
    private UUID id;
    private Instant placedAt;
    private double totalAmount;
    private List<PurchaseOrderItemDTO> items;
}
