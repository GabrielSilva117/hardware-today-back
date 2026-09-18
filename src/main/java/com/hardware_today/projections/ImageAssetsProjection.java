package com.hardware_today.projections;

public interface ImageAssetsProjection {
    String getMiniature();
    String getGallery();
    String getDetail();
    String getAltText();
    boolean isActive();
}
