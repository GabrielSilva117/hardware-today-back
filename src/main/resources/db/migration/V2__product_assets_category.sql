-- ============================================================
-- V2__product_assets_category.sql
-- Replaces the ProductAssets 1:1-product-asset model with:
--   images          -- standalone image entity (alt_text, active,
--                       detail, miniature, gallery, created_at, updated_at)
--   categories      -- gains image_id (1:1 -> images)
--   product_assets  -- becomes the Product <-> Image many-to-many
--                       join table (drops old inline asset columns
--                       and its FK/index tied to the old architecture)
-- ============================================================

-- ---------------------------------------------------------------
-- drop old product_assets table (1:1-ish product asset row with
-- inline image columns, FK to products only)
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS product_assets;

-- ---------------------------------------------------------------
-- images
-- ---------------------------------------------------------------
CREATE TABLE images (
    id         UUID PRIMARY KEY,
    alt_text   VARCHAR(255),
    active     BOOLEAN NOT NULL,
    detail     VARCHAR(255),
    miniature  VARCHAR(255),
    gallery    VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------
-- product_assets  (join table, -> products, images)
-- ---------------------------------------------------------------
CREATE TABLE product_assets (
    product_id UUID NOT NULL,
    image_id   UUID NOT NULL,
    CONSTRAINT pk_product_assets PRIMARY KEY (product_id, image_id),
    CONSTRAINT fk_product_assets_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_product_assets_image FOREIGN KEY (image_id) REFERENCES images (id)
);

CREATE INDEX idx_product_assets_image_id ON product_assets (image_id);

-- ---------------------------------------------------------------
-- categories  (-> images, 1:1)
-- ---------------------------------------------------------------
ALTER TABLE categories
    ADD COLUMN image_id UUID;

ALTER TABLE categories
    ADD CONSTRAINT fk_categories_image FOREIGN KEY (image_id) REFERENCES images (id);

ALTER TABLE categories
    ADD CONSTRAINT uq_categories_image UNIQUE (image_id);


-- ---------------------------------------------------------------
-- brands  (-> images, 1:1)
-- ---------------------------------------------------------------
ALTER TABLE brands
    ADD COLUMN image_id UUID;

ALTER TABLE brands
    ADD CONSTRAINT fk_brands_image FOREIGN KEY (image_id) REFERENCES images (id);

ALTER TABLE brands
    ADD CONSTRAINT uq_brands_image UNIQUE (image_id);
