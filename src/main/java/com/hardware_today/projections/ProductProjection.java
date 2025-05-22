package com.hardware_today.projections;

import java.util.UUID;

public interface ProductProjection {
	UUID getId();
	String getName();
	double getPrice();
	CategoryProjection getCategory();
}
