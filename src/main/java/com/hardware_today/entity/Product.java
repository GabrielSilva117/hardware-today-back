package com.hardware_today.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Index;

//, indexes = {
//        @Index(name = "idx_product_brand", columnList = "brand_id"),
//        @Index(name = "idx_product_category", columnList = "category_id")
//}
@Entity(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    private Category category;

    @ManyToOne(optional = false)
    private Brand brand;

    @ManyToMany
    @JoinTable(
            name = "product_assets",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private Set<Image> images = new HashSet<>();

    private String name;

    private double price;

    private String description;
}