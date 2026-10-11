CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    name VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(255),
    uid VARCHAR(255) NOT NULL,
    photo_profile VARCHAR(255),
    CONSTRAINT uk_users_uid UNIQUE (uid)
);

CREATE INDEX idx_users_phone_number ON users (phone_number);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_user_roles_user_role UNIQUE (user_id, role)
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    uid VARCHAR(255),
    product_name VARCHAR(255),
    selling_price NUMERIC(38, 0),
    initial_price NUMERIC(38, 0),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_products_uid_item_name ON products (uid, product_name);

CREATE TABLE inventories (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    product_id UUID NOT NULL,
    quantity INTEGER,
    version INTEGER NOT NULL,
    CONSTRAINT fk_inventories_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE SEQUENCE stock_movements_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE stock_movements (
    id BIGINT PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    stock_movement_type VARCHAR(30) NOT NULL,
    reverence_type VARCHAR(255),
    reference_id VARCHAR(255),
    CONSTRAINT fk_stock_movements_product FOREIGN KEY (product_id) REFERENCES products (id)
);
