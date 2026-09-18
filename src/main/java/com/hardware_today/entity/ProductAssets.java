package com.hardware_today.entity;

import java.util.UUID;

import jakarta.persistence.*;
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

	private String altText;

	private boolean active;

	@OneToOne(optional=false)
	private Product product;
	
	private String detail;
	
	private String miniature;
	
	private String gallery;
}
