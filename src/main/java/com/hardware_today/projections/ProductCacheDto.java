package com.hardware_today.projections;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.hardware_today.utils.ImageUrlResolver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Concrete, Jackson-serializable stand-in for {@link ProductProjection} used when caching product pages in Redis. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCacheDto implements ProductProjection, Serializable {
    private UUID id;
    private String name;
    private double price;
    private CategoryCacheDto category;
    private List<ImageProjection> images;

    public static ProductCacheDto from(ProductProjection source, ImageUrlResolver urlResolver) {
        return new ProductCacheDto(
                source.getId(),
                source.getName(),
                source.getPrice(),
                CategoryCacheDto.from(source.getCategory(), urlResolver),
                source.getImages() == null
                        ? List.<ImageProjection>of()
                        : source.getImages().stream().<ImageProjection>map(img -> ImageCacheDto.from(img, urlResolver)).toList()
        );
    }
}
