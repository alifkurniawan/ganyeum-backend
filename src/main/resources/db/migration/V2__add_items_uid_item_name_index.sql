DROP INDEX IF EXISTS idx_items_uid;

CREATE INDEX idx_items_uid_item_name
    ON items (uid, item_name);