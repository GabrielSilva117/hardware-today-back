package com.hardware_today.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ProductFilterModel {
    private String minPrice;
    private String maxPrice;
    private String brandUUIDs;
    private String categoryUUIDs;
}
