-- Normalize existing phone values to a stable representation before enforcing uniqueness.
UPDATE users
SET phone_number = regexp_replace(trim(phone_number), '[[:space:]().-]', '', 'g')
WHERE phone_number IS NOT NULL;

CREATE UNIQUE INDEX uk_users_phone_number_not_null
    ON users (phone_number)
    WHERE phone_number IS NOT NULL;

CREATE SEQUENCE customers_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    seller_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    CONSTRAINT fk_customers_seller FOREIGN KEY (seller_id) REFERENCES users (id),
    CONSTRAINT fk_customers_customer FOREIGN KEY (customer_id) REFERENCES users (id),
    CONSTRAINT uk_customers_seller_customer UNIQUE (seller_id, customer_id),
    CONSTRAINT ck_customers_seller_not_customer CHECK (seller_id <> customer_id)
);
CREATE INDEX idx_customers_customer ON customers (customer_id);

CREATE SEQUENCE shipping_methods_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE shipping_methods (
    id BIGINT PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    order_id VARCHAR(64) NOT NULL UNIQUE,
    seller_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone_number VARCHAR(255) NOT NULL,
    customer_address TEXT NOT NULL,
    total_price NUMERIC(38, 0) NOT NULL CHECK (total_price >= 0),
    shipping_id BIGINT,
    shipping_name VARCHAR(255),
    invoice_id VARCHAR(64) NOT NULL UNIQUE,
    invoice_issued_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_purchase_orders_seller FOREIGN KEY (seller_id) REFERENCES users (id),
    CONSTRAINT fk_purchase_orders_customer FOREIGN KEY (customer_id) REFERENCES users (id),
    CONSTRAINT fk_purchase_orders_shipping FOREIGN KEY (shipping_id) REFERENCES shipping_methods (id),
    CONSTRAINT ck_purchase_orders_shipping_pair CHECK ((shipping_id IS NULL) = (shipping_name IS NULL))
);
CREATE INDEX idx_purchase_orders_seller_created ON purchase_orders (seller_id, created_at);
CREATE INDEX idx_purchase_orders_customer ON purchase_orders (customer_id);

CREATE SEQUENCE shipping_details_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE shipping_details (
    id BIGINT PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    purchase_order_id UUID NOT NULL UNIQUE,
    shipping_cost NUMERIC(38, 0) NOT NULL CHECK (shipping_cost >= 0),
    CONSTRAINT fk_shipping_details_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders (id)
);

CREATE SEQUENCE purchase_order_details_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE purchase_order_details (
    id BIGINT PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    purchase_order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price_per_item NUMERIC(38, 0) NOT NULL CHECK (price_per_item >= 0),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    total_price NUMERIC(38, 0) NOT NULL CHECK (total_price >= 0),
    CONSTRAINT fk_purchase_order_details_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders (id),
    CONSTRAINT fk_purchase_order_details_product FOREIGN KEY (product_id) REFERENCES products (id)
);
CREATE INDEX idx_purchase_order_details_order ON purchase_order_details (purchase_order_id);
CREATE INDEX idx_purchase_order_details_product ON purchase_order_details (product_id);
