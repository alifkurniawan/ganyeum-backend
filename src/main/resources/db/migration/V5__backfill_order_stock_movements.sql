-- Identify orders whose stock deduction has not yet been recorded.
CREATE TEMPORARY TABLE order_stock_backfill ON COMMIT DROP AS
SELECT po.id AS purchase_order_id,
       pod.product_id,
       pod.quantity
FROM purchase_orders po
JOIN purchase_order_details pod ON pod.purchase_order_id = po.id
WHERE NOT EXISTS (
    SELECT 1
    FROM stock_movements sm
    WHERE sm.stock_movement_type = 'SALE'
      AND sm.reverence_type = 'PURCHASE_ORDER'
      AND sm.reference_id = po.id::text
);

-- Reduce existing balances by the quantities sold in the identified orders.
UPDATE inventories i
SET quantity = COALESCE(i.quantity, 0) - sold.quantity,
    updated_at = CURRENT_TIMESTAMP
FROM (
    SELECT product_id, SUM(quantity)::INTEGER AS quantity
    FROM order_stock_backfill
    GROUP BY product_id
) sold
WHERE i.product_id = sold.product_id;

-- Create inventory rows for sold products that do not have a balance row yet.
INSERT INTO inventories (id, created_at, updated_at, deleted_at, product_id, quantity, version)
SELECT gen_random_uuid(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL,
       sold.product_id, -sold.quantity, 0
FROM (
    SELECT product_id, SUM(quantity)::INTEGER AS quantity
    FROM order_stock_backfill
    GROUP BY product_id
) sold
WHERE NOT EXISTS (
    SELECT 1 FROM inventories i WHERE i.product_id = sold.product_id
);

-- Record each order line as a SALE movement linked to its purchase order.
INSERT INTO stock_movements (
    id, created_at, updated_at, product_id, quantity,
    stock_movement_type, reverence_type, reference_id
)
SELECT nextval('stock_movements_seq'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
       product_id, -quantity, 'SALE', 'PURCHASE_ORDER', purchase_order_id::text
FROM order_stock_backfill;
