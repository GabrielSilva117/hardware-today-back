package com.hardware_today.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "cart_item",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_cart_item",
            columnNames = {"cart_id", "product_id"}
        )
    }
)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cart cart;

    @ManyToOne(optional = false)
    private Product product;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer quantity = 1;

    public CartItem(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
        this.quantity = 1;
    }
}
