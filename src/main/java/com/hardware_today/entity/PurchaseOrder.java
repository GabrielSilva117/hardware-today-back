package com.hardware_today.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrder {

    public PurchaseOrder(User user, Instant placedAt, double totalAmount) {
        this.user = user;
        this.placedAt = placedAt;
        this.totalAmount = totalAmount;
        this.items = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    private User user;

    private Instant placedAt;

    private double totalAmount;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PurchaseOrderItem> items;
}
