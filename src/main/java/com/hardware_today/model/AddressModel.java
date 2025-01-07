package com.hardware_today.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class AddressModel {
    private String cep;

    private String address;

    private String neighborhood;

    private String city;

    private String state;
}
