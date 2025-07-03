CREATE TABLE IF NOT EXISTS customers (
    customer_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    address VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(19, 2) NOT NULL,
    category VARCHAR(255),
    sku VARCHAR(255) UNIQUE
    );

CREATE TABLE IF NOT EXISTS wishlist_items (
    wishlist_item_id VARCHAR(255) PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    added_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_wishlist_customer FOREIGN KEY (customer_id) REFERENCES customers (customer_id),
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products (product_id)
    );

-- New tables for sales
CREATE TABLE IF NOT EXISTS sales (
    sale_id VARCHAR(255) PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    sale_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
    );

CREATE TABLE IF NOT EXISTS sale_items (
    sale_item_id VARCHAR(255) PRIMARY KEY,
    sale_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price_at_sale NUMERIC(19, 2) NOT NULL,
    item_total NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sales (sale_id),
    CONSTRAINT fk_sale_item_product FOREIGN KEY (product_id) REFERENCES products (product_id)
    );