package com.hardware_today.entity;

import com.hardware_today.model.AddressModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressEntity {
    public AddressEntity(AddressModel model) {
        this.cep = model.getCep();
        this.address = model.getAddress();
        this.neighborhood = model.getNeighborhood();
        this.city = model.getCity();
        this.state = model.getState();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cep;

    private String address;

    private String neighborhood;

    private String city;

    private String state;
}
