-- Backfill: one placeholder image per category still missing one,
-- linked via categories.image_id (idempotent, safe to re-run).
WITH cat_images AS (
    SELECT c.id AS category_id, c.name AS category_name, gen_random_uuid() AS image_id
    FROM categories c
    WHERE c.image_id IS NULL
),
inserted AS (
    INSERT INTO images (id, alt_text, active, detail, miniature, gallery, created_at, updated_at)
    SELECT image_id, category_name, true, NULL, NULL, NULL, now(), now()
    FROM cat_images
    RETURNING id
)
UPDATE categories c
SET image_id = ci.image_id
FROM cat_images ci
WHERE c.id = ci.category_id;
