package com.hardware_today.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Cart {	
	public Cart() {
		this.items = new HashSet<>();
		this.enabled = true;
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ColumnDefault("true")
	private boolean enabled;

	@JsonIgnore
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CartItem> items;
	
	@ManyToOne(optional=false)
	private User user;
}
