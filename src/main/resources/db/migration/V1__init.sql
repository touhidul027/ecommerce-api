-- Create product table
CREATE TABLE product (
                         id SERIAL PRIMARY KEY,
                         product_id VARCHAR(255) NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         description VARCHAR(1000),
                         price NUMERIC(10,2) NOT NULL,
                         category VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP NOT NULL
);

-- Create wish_list table
CREATE TABLE wish_list (
                           id SERIAL PRIMARY KEY,
                           customer_id VARCHAR(255) NOT NULL,
                           product_id VARCHAR(255) NOT NULL,
                           created_at TIMESTAMP NOT NULL
);

-- Insert initial product data
INSERT INTO product (product_id, name, description, price, category, created_at) VALUES
                                                                                     ('prod-001', 'Smartphone X', 'Latest model with advanced camera', 999.99, 'Electronics', NOW()),
                                                                                     ('prod-002', 'Wireless Headphones', 'Noise-cancelling Bluetooth headphones', 199.99, 'Electronics', NOW()),
                                                                                     ('prod-003', 'Running Shoes', 'Lightweight shoes for marathon running', 89.99, 'Sports', NOW());

-- Insert initial wishlist data
INSERT INTO wish_list (customer_id, product_id, created_at) VALUES
                                                                ('cust-001', 'prod-001', NOW()),
                                                                ('cust-001', 'prod-002', NOW());
