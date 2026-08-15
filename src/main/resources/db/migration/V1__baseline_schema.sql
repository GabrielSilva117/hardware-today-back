-- ============================================================
-- V1__baseline_schema.sql
-- Baseline schema snapshot generated from current @Entity classes
-- (hardware-today monolith)
--
-- Order respects FK dependencies:
--   addresses, brands, categories, roles  (no deps)
--   users                                  (-> addresses)
--   user_roles                             (-> users, roles)
--   products                               (-> brands, categories)
--   product_assets                         (-> products)
--   cart                                   (-> users)
--   cart_item                              (-> cart, products)
--   purchase_order                         (-> users)
--   purchase_order_item                    (-> purchase_order)
-- ============================================================

-- ---------------------------------------------------------------
-- addresses
-- ---------------------------------------------------------------
CREATE TABLE addresses (
    id           UUID PRIMARY KEY,
    cep          VARCHAR(255),
    address      VARCHAR(255),
    neighborhood VARCHAR(255),
    city         VARCHAR(255),
    state        VARCHAR(255)
);

-- ---------------------------------------------------------------
-- brands
-- ---------------------------------------------------------------
CREATE TABLE brands (
    id   UUID PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    CONSTRAINT uq_brands_name UNIQUE (name)
);

-- ---------------------------------------------------------------
-- categories
-- ---------------------------------------------------------------
CREATE TABLE categories (
    id   UUID PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

-- ---------------------------------------------------------------
-- roles
-- ---------------------------------------------------------------
CREATE TABLE roles (
    id   UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT uq_roles_name UNIQUE (name)
);

-- ---------------------------------------------------------------
-- users  (-> addresses)
-- ---------------------------------------------------------------
CREATE TABLE users (
    id         UUID PRIMARY KEY,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255),
    phone      VARCHAR(255),
    password   VARCHAR(255),
    enabled    BOOLEAN NOT NULL DEFAULT TRUE,
    address_id UUID,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT fk_users_address FOREIGN KEY (address_id) REFERENCES addresses (id)
);

CREATE INDEX idx_users_address_id ON users (address_id);

-- ---------------------------------------------------------------
-- user_roles  (join table, -> users, roles)
-- ---------------------------------------------------------------
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

-- ---------------------------------------------------------------
-- products  (-> brands, categories)
-- ---------------------------------------------------------------
CREATE TABLE products (
    id          UUID PRIMARY KEY,
    category_id UUID NOT NULL,
    brand_id    UUID NOT NULL,
    name        VARCHAR(255),
    price       DOUBLE PRECISION NOT NULL,
    -- NOTE: mapped from a plain `String description` with no @Column length
    -- override, so Hibernate defaults this to varchar(255). Kept as-is here
    -- to match the current entity exactly; consider a V2 migration to widen
    -- this to TEXT if product descriptions need more room.
    description VARCHAR(255),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_products_brand FOREIGN KEY (brand_id) REFERENCES brands (id)
);

CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_products_brand_id ON products (brand_id);
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_product_name_trgm        ON products USING GIN (name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_product_description_trgm ON products USING GIN (description gin_trgm_ops);

-- ---------------------------------------------------------------
-- product_assets  (-> products)
-- ---------------------------------------------------------------
CREATE TABLE product_assets (
    id         UUID PRIMARY KEY,
    alt_text   VARCHAR(255),
    active     BOOLEAN NOT NULL,
    product_id UUID NOT NULL,
    detail     VARCHAR(255),
    miniature  VARCHAR(255),
    gallery    VARCHAR(255),
    CONSTRAINT fk_product_assets_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE INDEX idx_product_assets_product_id ON product_assets (product_id);

-- ---------------------------------------------------------------
-- cart  (-> users)
-- ---------------------------------------------------------------
CREATE TABLE cart (
    id      UUID PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    user_id UUID NOT NULL,
    name    VARCHAR(255),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_cart_user_id ON cart (user_id);

-- ---------------------------------------------------------------
-- cart_item  (-> cart, products)
-- ---------------------------------------------------------------
CREATE TABLE cart_item (
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    cart_id    UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity   INTEGER NOT NULL DEFAULT 1,
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart (id),
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT uq_cart_item UNIQUE (cart_id, product_id)
);

CREATE INDEX idx_cart_item_product_id ON cart_item (product_id);

-- ---------------------------------------------------------------
-- purchase_order  (-> users)
-- ---------------------------------------------------------------
CREATE TABLE purchase_order (
    id           UUID PRIMARY KEY,
    user_id      UUID NOT NULL,
    placed_at    TIMESTAMP,
    total_amount DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_purchase_order_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_purchase_order_user_id ON purchase_order (user_id);

-- ---------------------------------------------------------------
-- purchase_order_item  (-> purchase_order)
-- NOTE: product_id here is a plain UUID column, NOT a foreign key --
-- the entity stores a denormalized snapshot (product_id, product_name,
-- unit_price) rather than a live @ManyToOne to Product. This is
-- intentional order-history behavior, so no FK constraint is added.
-- ---------------------------------------------------------------
CREATE TABLE purchase_order_item (
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    purchase_order_id UUID NOT NULL,
    product_id        UUID,
    product_name      VARCHAR(255),
    unit_price        DOUBLE PRECISION NOT NULL,
    quantity          INTEGER NOT NULL,
    CONSTRAINT fk_purchase_order_item_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_order (id)
);

CREATE INDEX idx_purchase_order_item_order_id ON purchase_order_item (purchase_order_id);