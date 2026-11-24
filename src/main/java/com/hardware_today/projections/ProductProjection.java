package com.hardware_today.projections;

import java.util.List;
import java.util.UUID;

public interface ProductProjection {
	UUID getId();
	String getName();
	double getPrice();
	CategoryProjection getCategory();
	List<ImageProjection> getImages();
}
