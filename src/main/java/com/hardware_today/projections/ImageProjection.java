package com.hardware_today.projections;

public interface ImageProjection {
    String getMiniature();
    String getGallery();
    String getDetail();
    String getAltText();
    boolean isActive();
}
