package com.hardware_today.projections;

import java.util.List;

public interface CartItemProjection {
    ProductProjection getProduct();
    Long getQuantity();
}
