ALTER TABLE purchase_orders
    ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT 'PENDING';

ALTER TABLE purchase_orders
    ADD CONSTRAINT ck_purchase_orders_status
    CHECK (status IN (
        'PENDING',
        'CONFIRMED',
        'PROCESSING',
        'READY_FOR_PICKUP',
        'SHIPPED',
        'COMPLETED',
        'CANCELLED'
    ));

CREATE INDEX idx_purchase_orders_seller_status_created
    ON purchase_orders (seller_id, status, created_at);

ALTER TABLE purchase_order_details
    ADD COLUMN initial_cost_per_item NUMERIC(38, 0),
    ADD COLUMN total_initial_cost NUMERIC(38, 0);

UPDATE purchase_order_details pod
SET initial_cost_per_item = COALESCE(p.initial_price, 0),
    total_initial_cost = COALESCE(p.initial_price, 0) * pod.quantity
FROM products p
WHERE p.id = pod.product_id;

ALTER TABLE purchase_order_details
    ALTER COLUMN initial_cost_per_item SET NOT NULL,
    ALTER COLUMN total_initial_cost SET NOT NULL,
    ADD CONSTRAINT ck_order_details_initial_cost CHECK (initial_cost_per_item >= 0),
    ADD CONSTRAINT ck_order_details_total_initial_cost CHECK (total_initial_cost >= 0);

ALTER TABLE purchase_orders
    ADD COLUMN total_initial_cost NUMERIC(38, 0),
    ADD COLUMN profit NUMERIC(38, 0);

UPDATE purchase_orders po
SET total_initial_cost = COALESCE((
        SELECT SUM(pod.total_initial_cost)
        FROM purchase_order_details pod
        WHERE pod.purchase_order_id = po.id
    ), 0),
    profit = COALESCE((
        SELECT SUM(pod.total_price - pod.total_initial_cost)
        FROM purchase_order_details pod
        WHERE pod.purchase_order_id = po.id
    ), 0);

ALTER TABLE purchase_orders
    ALTER COLUMN total_initial_cost SET NOT NULL,
    ALTER COLUMN profit SET NOT NULL,
    ADD CONSTRAINT ck_purchase_orders_total_initial_cost CHECK (total_initial_cost >= 0);
