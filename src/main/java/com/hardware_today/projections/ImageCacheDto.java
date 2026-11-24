package com.hardware_today.projections;

import java.io.Serializable;

import com.hardware_today.utils.ImageUrlResolver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Concrete, Jackson-serializable stand-in for {@link ImageProjection} used when caching product pages in Redis. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageCacheDto implements ImageProjection, Serializable {
    private String miniature;
    private String gallery;
    private String detail;
    private String altText;
    private boolean active;

    public static ImageCacheDto from(ImageProjection source, ImageUrlResolver urlResolver) {
        if (source == null) return null;
        return new ImageCacheDto(
                urlResolver.resolve(source.getMiniature()),
                urlResolver.resolve(source.getGallery()),
                urlResolver.resolve(source.getDetail()),
                source.getAltText(),
                source.isActive()
        );
    }
}
