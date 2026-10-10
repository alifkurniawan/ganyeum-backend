CREATE TABLE items (
    id UUID PRIMARY KEY,
    uid VARCHAR(255),
    item_name VARCHAR(255),
    selling_price NUMERIC(38, 0),
    initial_price NUMERIC(38, 0),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_items_uid ON items (uid);

CREATE TABLE inventories (
    id UUID PRIMARY KEY,
    item_id UUID NOT NULL,
    quantity INTEGER,
    version INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_inventories_item FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE stock_movements (
    id BIGINT PRIMARY KEY,
    item_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    stock_movement_type VARCHAR(30) NOT NULL,
    reverence_type VARCHAR(255),
    reference_id VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_stock_movements_item FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE SEQUENCE stock_movements_seq
    START WITH 1
    INCREMENT BY 50;

ALTER TABLE stock_movements
    ALTER COLUMN id SET DEFAULT nextval('stock_movements_seq');
