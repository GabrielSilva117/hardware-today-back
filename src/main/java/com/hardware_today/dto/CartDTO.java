package com.hardware_today.dto;

import java.util.List;
import java.util.UUID;

import com.hardware_today.projections.ProductProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CartDTO {
	private UUID id;
	private boolean enabled;
	private List<ProductProjection> products;
	private double totalPrice;
}
