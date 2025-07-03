-- Insert Customers
INSERT INTO customers (customer_id, name, email, address)
VALUES
    ('cust-001', 'Touhidul Islam', 'touhidul027@example.com', '123 Main St'),
    ('cust-002', 'Jane Doe', 'jane.doe@example.com', '456 Oak Ave');

-- Insert Products
INSERT INTO product (product_id, name, description, price, category, created_at)
VALUES
    ('prod-001', 'Laptop Pro', 'High-performance laptop', 1500.00, 'Electronics',  NOW()),
    ('prod-002', 'Mechanical Keyboard', 'RGB mechanical keyboard', 120.00, 'Accessories',  NOW()),
    ('prod-003', 'Wireless Mouse', 'Ergonomic wireless mouse', 35.00, 'Accessories',  NOW()),
    ('prod-004', 'Monitor Ultra', '4K Gaming Monitor', 600.00, 'Electronics',  NOW());

-- Insert Wishlist Items
INSERT INTO wish_list (customer_id, product_id, created_at)
VALUES
    ('cust-001', 'prod-001', NOW() - INTERVAL '5 days'),
    ( 'cust-001', 'prod-002', NOW() - INTERVAL '2 days'),
    ( 'cust-002', 'prod-003', NOW() - INTERVAL '1 day');

-- Insert Sales
INSERT INTO sales (sale_id, customer_id, sale_date, total_amount, status)
VALUES
    ('sale-001', 'cust-001', NOW()::DATE + INTERVAL '10 hours', 1620.00, 'COMPLETED'),
    ('sale-002', 'cust-002', NOW()::DATE + INTERVAL '14 hours', 635.00, 'COMPLETED'),
    ('sale-003', 'cust-001', (NOW()::DATE - INTERVAL '1 day') + INTERVAL '11 hours', 1500.00, 'COMPLETED'),
    ('sale-004', 'cust-002', (NOW()::DATE - INTERVAL '2 days') + INTERVAL '9 hours', 120.00, 'COMPLETED');
INSERT INTO public.sales (sale_id, customer_id, sale_date, total_amount, status) VALUES ('sale-005', 'cust-001', '2025-06-03 10:00:00.000000', 1620.00, 'COMPLETED');
INSERT INTO public.sales (sale_id, customer_id, sale_date, total_amount, status) VALUES ('sale-006', 'cust-002', '2025-06-03 14:00:00.000000', 635.00, 'COMPLETED');
INSERT INTO public.sales (sale_id, customer_id, sale_date, total_amount, status) VALUES ('sale-007', 'cust-001', '2025-06-02 11:00:00.000000', 1500.00, 'COMPLETED');
INSERT INTO public.sales (sale_id, customer_id, sale_date, total_amount, status) VALUES ('sale-008', 'cust-002', '2025-06-01 09:00:00.000000', 120.00, 'COMPLETED');



-- Insert Sale Items
-- Sale 1 Items
INSERT INTO sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total)
VALUES
    ('item-001', 'sale-001', 'prod-001', 1, 1500.00, 1500.00),
    ('item-002', 'sale-001', 'prod-002', 1, 120.00, 120.00);

-- Sale 2 Items
INSERT INTO sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total)
VALUES
    ('item-003', 'sale-002', 'prod-003', 1, 35.00, 35.00),
    ('item-004', 'sale-002', 'prod-004', 1, 600.00, 600.00);

-- Sale 3 Items
INSERT INTO sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total)
VALUES
    ('item-005', 'sale-003', 'prod-001', 1, 1500.00, 1500.00);

-- Sale 4 Items
INSERT INTO sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total)
VALUES
    ('item-006', 'sale-004', 'prod-002', 1, 120.00, 120.00);

INSERT INTO public.sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total) VALUES ('item-007', 'sale-005', 'prod-001', 1, 1500.00, 1500.00);
INSERT INTO public.sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total) VALUES ('item-008', 'sale-005', 'prod-002', 1, 120.00, 120.00);
INSERT INTO public.sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total) VALUES ('item-009', 'sale-005', 'prod-003', 1, 35.00, 35.00);
INSERT INTO public.sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total) VALUES ('item-010', 'sale-006', 'prod-004', 1, 600.00, 600.00);
INSERT INTO public.sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total) VALUES ('item-011', 'sale-006', 'prod-001', 1, 1500.00, 1500.00);
INSERT INTO public.sale_items (sale_item_id, sale_id, product_id, quantity, unit_price_at_sale, item_total) VALUES ('item-012', 'sale-006', 'prod-002', 1, 120.00, 120.00);
