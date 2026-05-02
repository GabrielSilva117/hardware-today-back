package com.hardware_today.enums;

import lombok.Getter;

@Getter
public enum PaymentType {
    CARD("card");

    private final String path;

    // Constructor
    PaymentType(String path) {
        this.path = path;
    }
}
