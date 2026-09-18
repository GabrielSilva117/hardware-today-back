package com.hardware_today.entity;

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

    @OneToOne(mappedBy = "product")
    private ProductAssets assets;

    private String name;

    private double price;

    private String description;
}