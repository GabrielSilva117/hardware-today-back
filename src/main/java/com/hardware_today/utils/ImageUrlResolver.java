package com.hardware_today.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Turns an object key stored on {@code Image} (miniature/gallery/detail) into a deliverable URL. */
@Component
public class ImageUrlResolver {
    private final String baseUrl;

    public ImageUrlResolver(@Value("${app.images.base-url}") String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public String resolve(String key) {
        if (key == null || key.isBlank()) return null;
        return baseUrl + "/" + key;
    }
}
