package com.hardware_today.projections;

import java.util.List;
import java.util.UUID;

public interface CartProjection {
	UUID getId();
	boolean getEnabled();
	List<ProductProjection> getProducts();
}
