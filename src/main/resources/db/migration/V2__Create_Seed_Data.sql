-- Insert Customers
INSERT INTO customers (customer_id, name, email, address)
SELECT 'cust-001', 'Touhidul Islam', 'touhidul027@example.com', '123 Main St'
    WHERE NOT EXISTS (
    SELECT 1 FROM customers WHERE customer_id = 'cust-001'
);

INSERT INTO customers (customer_id, name, email, address)
SELECT 'cust-002', 'Jane Doe', 'jane.doe@example.com', '456 Oak Ave'
    WHERE NOT EXISTS (
    SELECT 1 FROM customers WHERE customer_id = 'cust-002'
);

-- Insert Products
INSERT INTO product (product_id, name, description, price, category, sku, created_at)
SELECT 'prod-001', 'Laptop Pro', 'High-performance laptop', 1500.00, 'Electronics', 'LAPTOPPRO001', NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE product_id = 'prod-001'
);

INSERT INTO product (product_id, name, description, price, category, sku, created_at)
SELECT 'prod-002', 'Mechanical Keyboard', 'RGB mechanical keyboard', 120.00, 'Accessories', 'KEYBOARD001', NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE product_id = 'prod-002'
);

INSERT INTO product (product_id, name, description, price, category, sku, created_at)
SELECT 'prod-003', 'Wireless Mouse', 'Ergonomic wireless mouse', 35.00, 'Accessories', 'MOUSE001', NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE product_id = 'prod-003'
);

INSERT INTO product (product_id, name, description, price, category, sku, created_at)
SELECT 'prod-004', 'Monitor Ultra', '4K Gaming Monitor', 600.00, 'Electronics', 'MONITOR001', NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM product WHERE product_id = 'prod-004'
);

-- Insert Wishlist Items
INSERT INTO wishlist_item (wishlist_item_id, customer_id, product_id, created_at)
SELECT 'wish-001', 'cust-001', 'prod-001', NOW() - INTERVAL '5 days'
WHERE NOT EXISTS (
    SELECT 1 FROM wishlist_item WHERE wishlist_item_id = 'wish-001'
    );

INSERT INTO wishlist_item (wishlist_item_id, customer_id, product_id, created_at)
SELECT 'wish-002', 'cust-001', 'prod-002', NOW() - INTERVAL '2 days'
WHERE NOT EXISTS (
    SELECT 1 FROM wishlist_item WHERE wishlist_item_id = 'wish-002'
    );

INSERT INTO wishlist_item (wishlist_item_id, customer_id, product_id, created_at)
SELECT 'wish-003', 'cust-002', 'prod-003', NOW() - INTERVAL '1 day'
WHERE NOT EXISTS (
    SELECT 1 FROM wishlist_item WHERE wishlist_item_id = 'wish-003'
    );

-- Insert Sales and Sale Items
-- Sale 1 (Today)
INSERT INTO sale (sale_id, customer_id, created_at, total_price, status)
SELECT 'sale-001', 'cust-001', NOW()::date + INTERVAL '10 hours', 1620.00, 'COMPLETED'
WHERE NOT EXISTS (
    SELECT 1 FROM sale WHERE sale_id = 'sale-001'
    );

INSERT INTO sale_item (sale_item_id, sale_id, product_id, quantity, unit_price, total_price)
SELECT 'si-001', 'sale-001', 'prod-001', 1, 1500.00, 1500.00
    WHERE NOT EXISTS (
    SELECT 1 FROM sale_item WHERE sale_item_id = 'si-001'
);

INSERT INTO sale_item (sale_item_id, sale_id, product_id, quantity, unit_price, total_price)
SELECT 'si-002', 'sale-001', 'prod-002', 1, 120.00, 120.00
    WHERE NOT EXISTS (
    SELECT 1 FROM sale_item WHERE sale_item_id = 'si-002'
);

-- Sale 2 (Today)
INSERT INTO sale (sale_id, customer_id, created_at, total_price, status)
SELECT 'sale-002', 'cust-002', NOW()::date + INTERVAL '14 hours', 635.00, 'COMPLETED'
WHERE NOT EXISTS (
    SELECT 1 FROM sale WHERE sale_id = 'sale-002'
    );

INSERT INTO sale_item (sale_item_id, sale_id, product_id, quantity, unit_price, total_price)
SELECT 'si-003', 'sale-002', 'prod-003', 1, 35.00, 35.00
    WHERE NOT EXISTS (
    SELECT 1 FROM sale_item WHERE sale_item_id = 'si-003'
);

INSERT INTO sale_item (sale_item_id, sale_id, product_id, quantity, unit_price, total_price)
SELECT 'si-004', 'sale-002', 'prod-004', 1, 600.00, 600.00
    WHERE NOT EXISTS (
    SELECT 1 FROM sale_item WHERE sale_item_id = 'si-004'
);

-- Sale 3 (Yesterday)
INSERT INTO sale (sale_id, customer_id, created_at, total_price, status)
SELECT 'sale-003', 'cust-001', (NOW()::date - INTERVAL '1 day') + INTERVAL '11 hours', 1500.00, 'COMPLETED'
WHERE NOT EXISTS (
    SELECT 1 FROM sale WHERE sale_id = 'sale-003'
    );

INSERT INTO sale_item (sale_item_id, sale_id, product_id, quantity, unit_price, total_price)
SELECT 'si-005', 'sale-003', 'prod-001', 1, 1500.00, 1500.00
    WHERE NOT EXISTS (
    SELECT 1 FROM sale_item WHERE sale_item_id = 'si-005'
);

-- Sale 4 (Two days ago)
INSERT INTO sale (sale_id, customer_id, created_at, total_price, status)
SELECT 'sale-004', 'cust-002', (NOW()::date - INTERVAL '2 days') + INTERVAL '9 hours', 120.00, 'COMPLETED'
WHERE NOT EXISTS (
    SELECT 1 FROM sale WHERE sale_id = 'sale-004'
    );

INSERT INTO sale_item (sale_item_id, sale_id, product_id, quantity, unit_price, total_price)
SELECT 'si-006', 'sale-004', 'prod-002', 1, 120.00, 120.00
    WHERE NOT EXISTS (
    SELECT 1 FROM sale_item WHERE sale_item_id = 'si-006'
);
