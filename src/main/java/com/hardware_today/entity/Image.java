package com.hardware_today.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity(name = "images")
@Getter
@Setter
@NoArgsConstructor
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String altText;

	private boolean active;

	private String detail;

	private String miniature;

	private String gallery;

	@CreationTimestamp
	@Column(updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	private Instant updatedAt;

	@ManyToMany(mappedBy = "images")
	private Set<Product> products = new HashSet<>();
}
