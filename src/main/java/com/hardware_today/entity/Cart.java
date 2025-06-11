package com.hardware_today.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Cart {	
	public Cart() {
		this.products = new HashSet<>();
		this.enabled = true;
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ColumnDefault("true")
	private boolean enabled;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade(CascadeType.ALL)
	@JoinTable(
				name="cart_products",
				joinColumns = @JoinColumn(name="cart_id"),
				inverseJoinColumns = @JoinColumn(name="product_id")
			)
	private Set<Product> products;
	
	@ManyToOne(optional=false)
	private User user;
}
