package com.hardware_today.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="product_assets")
@Getter
@Setter
@NoArgsConstructor
public class ProductAssets {
	@Id
	@GeneratedValue(strategy= GenerationType.UUID)
	private UUID id;
	
	@ManyToOne(optional=false)
	private Product product;
	
	private String detail;
	
	private String miniature;
	
	private String gallery;
}
