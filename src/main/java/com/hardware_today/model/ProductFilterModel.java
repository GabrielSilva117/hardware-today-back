package com.hardware_today.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ProductFilterModel {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String brand;
    private String category;
    private Integer limit;
}
