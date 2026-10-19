ALTER TABLE shipping_methods
    ADD COLUMN requires_tracking BOOLEAN NOT NULL DEFAULT TRUE;

INSERT INTO shipping_methods (id, name, requires_tracking)
VALUES
    (1, 'Paxel', TRUE),
    (2, 'GoSend', TRUE),
    (3, 'GrabExpress', TRUE),
    (4, 'Self Delivery', FALSE),
    (5, 'Customer Pickup', FALSE);

SELECT setval('shipping_methods_seq', 50, TRUE);

ALTER TABLE shipping_details
    ADD COLUMN tracking_number VARCHAR(255);
