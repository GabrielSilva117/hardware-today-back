package com.hardware_today.model;

import java.io.Serializable;
import java.util.List;

import com.hardware_today.projections.ProductCacheDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Serializable snapshot of one product page, stored in Redis instead of a raw {@code Page}
 * (Spring's {@code PageImpl} has no default constructor / setters, so Jackson cannot rebuild it
 * from cached JSON).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CachedProductPage implements Serializable {
    private List<ProductCacheDto> content;
    private long totalElements;
}
