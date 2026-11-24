package com.hardware_today.projections;

import java.io.Serializable;
import java.util.UUID;

import com.hardware_today.utils.ImageUrlResolver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Concrete, Jackson-serializable stand-in for {@link CategoryProjection} used when caching product pages in Redis. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCacheDto implements CategoryProjection, Serializable {
    private UUID id;
    private String name;
    private ImageProjection image;

    public static CategoryCacheDto from(CategoryProjection source, ImageUrlResolver urlResolver) {
        if (source == null) return null;
        return new CategoryCacheDto(source.getId(), source.getName(), ImageCacheDto.from(source.getImage(), urlResolver));
    }
}
