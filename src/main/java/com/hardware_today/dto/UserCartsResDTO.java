package com.hardware_today.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserCartsResDTO {
    private CartDTO activeCart;
    private List<CartDTO> inactiveCarts;
}
