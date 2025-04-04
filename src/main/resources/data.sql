-- Inserting Product Data

-- Crisps (Row 1)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('A1', 'Ready Salted Crisps', 1.20, 10, 'Crisps', '/images/crisp_ready_salted.webp'),
    ('A2', 'Salt & Vinegar Crisps', 1.20, 5, 'Crisps', '/images/crisp_salt_vinegar.webp'),
    ('A3', 'Cheese & Onion Crisps', 1.20, 12, 'Crisps', '/images/crisp_cheese_onion.webp'),
    ('A4', 'Prawn Cocktail Crisps', 1.20, 10, 'Crisps', '/images/crisp_prawn_cocktail.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

-- Sweet Snacks (Row 2)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('B1', 'Chocolate Digestives', 1.50, 10, 'Sweet Snacks', '/images/chocolate_digestives.webp'),
    ('B2', 'Shortbread Biscuits', 1.50, 12, 'Sweet Snacks', '/images/shortbread_biscuits.webp'),
    ('B3', 'Chocolate Bar', 1.20, 15, 'Sweet Snacks', '/images/chocolate_bar.webp'),
    ('B4', 'Peanut Chocolate Bar', 1.50, 10, 'Sweet Snacks', '/images/peanut_chocolate_bar.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

-- Healthy Snacks (Row 3)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('C1', 'Mixed Nuts Packet', 1.80, 15, 'Healthy Snacks', '/images/mixed_nuts_packet.webp'),
    ('C2', 'Dried Fruit Mix', 1.80, 12, 'Healthy Snacks', '/images/dried_fruit_mix.webp'),
    ('C3', 'Cereal Bar', 1.50, 20, 'Healthy Snacks', '/images/cereal_bar.webp'),
    ('C4', 'Rice Cakes', 1.50, 10, 'Healthy Snacks', '/images/rice_cakes.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url), name = VALUES(name);

-- Health Products (Row 4)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('D1', 'Protein Bar', 2.00, 15, 'Health Products', '/images/protein_bar.webp'),
    ('D2', 'Energy Gel', 2.50, 10, 'Health Products', '/images/energy_gel.webp'),
    ('D3', 'Vitamin Gummies', 2.20, 12, 'Health Products', '/images/vitamin_gummies.webp'),
    ('D4', 'Energy Shot', 2.50, 8, 'Health Products', '/images/energy_shot.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

-- Canned Drinks (Row 5)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('E1', 'Cola 330ml', 1.50, 12, 'Canned Drinks', '/images/cola_can.webp'),
    ('E2', 'Lemonade 330ml', 1.50, 10, 'Canned Drinks', '/images/lemonade_can.webp'),
    ('E3', 'Sparkling Water 330ml', 1.20, 10, 'Canned Drinks', '/images/sparkling_water_can.webp'),
    ('E4', 'Energy Drink 250ml', 2.00, 10, 'Canned Drinks', '/images/energy_drink_can.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

-- Bottled Drinks (Row 6)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('F1', 'Still Water 500ml', 1.00, 15, 'Bottled Drinks', '/images/still_water.webp'),
    ('F2', 'Orange Juice 330ml', 1.80, 10, 'Bottled Drinks', '/images/orange_juice.webp'),
    ('F3', 'Apple Juice 330ml', 1.80, 12, 'Bottled Drinks', '/images/apple_juice.webp'),
    ('F4', 'Lemon Iced Tea 500ml', 1.80, 8, 'Bottled Drinks', '/images/lemon_iced_tea.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);



-- Inserting Admin User Account
INSERT INTO users (username, password, email, role)
VALUES ('admin', '$2b$10$rFzxvg4dckIixTQZQ7NPKufuomGwGCHTGTlJlaoR/a04Ofw7vpwgy', 'admin1@admin.com', 'ADMIN')
ON DUPLICATE KEY UPDATE username = username;

UPDATE products SET auto_stock_enabled = FALSE WHERE auto_stock_enabled IS NULL;



-- Transactions spread across months for 'new' user
/* INSERT INTO transactions (user, total_cost, transaction_date, change_given, payment_received) VALUES
('new', 3.75, '2025-09-18', 0.00, 5.00),
('new', 5.10, '2025-09-01', 0.00, 5.00),
('new', 6.90, '2025-09-20', 0.00, 5.00),
('new', 4.80, '2025-10-07', 0.00, 5.00),
('new', 5.30, '2025-11-10', 0.00, 5.00),
('new', 4.10, '2025-11-22', 0.00, 5.00),
('new', 7.00, '2025-12-03', 0.00, 5.00),
('new', 6.25, '2025-01-14', 0.00, 5.00),
('new', 8.90, '2025-08-25', 0.00, 5.00); */
