package com.hardware_today.dto;

import java.util.List;
import java.util.UUID;

import com.hardware_today.projections.CartItemProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CartDTO {
	private UUID id;
	private boolean enabled;
	private List<CartItemProjection> items;
	private double totalPrice;
}
