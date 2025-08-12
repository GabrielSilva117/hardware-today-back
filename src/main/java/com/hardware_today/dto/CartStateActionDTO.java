package com.hardware_today.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class CartStateActionDTO {
    private UUID cartToChange;
    private Boolean shouldMerge;
}
