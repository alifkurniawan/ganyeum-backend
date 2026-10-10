ALTER TABLE items
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

DROP INDEX IF EXISTS idx_items_uid_item_name;

CREATE INDEX idx_items_id_active_item_name
    ON items (id, active, item_name);
