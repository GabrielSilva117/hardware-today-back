package com.hardware_today.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserModel {
    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String password;

    private AddressModel address;
}
