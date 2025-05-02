-- Inserting Product Data

-- Crisps (Row 1)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('A1', 'Ready Salted Crisps', 1.20, 10, 'Crisps', '/images/crisp_ready_salted.png'),
    ('A2', 'Salt & Vinegar Crisps', 1.20, 5, 'Crisps', '/images/crisp_salt_vinegar.png'),
    ('A3', 'Cheese & Onion Crisps', 1.20, 12, 'Crisps', '/images/crisp_cheese_onion.png'),
    ('A4', 'Prawn Cocktail Crisps', 1.20, 10, 'Crisps', '/images/crisp_prawn_cocktail.png')
    ON DUPLICATE KEY UPDATE id = VALUES(id); -- Prevents data being re-added in (only inserts the data on the first run, after that it doesn't insert)

-- Sweet Snacks (Row 2)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('B1', 'Chocolate Digestives', 1.50, 10, 'Sweet Snacks', '/images/chocolate_digestives.png'),
    ('B2', 'Shortbread Biscuits', 1.50, 12, 'Sweet Snacks', '/images/shortbread_biscuits.png'),
    ('B3', 'Chocolate Bar', 1.20, 15, 'Sweet Snacks', '/images/chocolate_bar.png'),
    ('B4', 'Peanut Chocolate Bar', 1.50, 10, 'Sweet Snacks', '/images/peanut_chocolate_bar.png')
    ON DUPLICATE KEY UPDATE id = VALUES(id);

-- Healthy Snacks (Row 3)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('C1', 'Mixed Nuts Packet', 1.80, 15, 'Healthy Snacks', '/images/mixed_nuts_packet.png'),
    ('C2', 'Dried Fruit Mix', 1.80, 12, 'Healthy Snacks', '/images/dried_fruit_mix.png'),
    ('C3', 'Cereal Bar', 1.50, 20, 'Healthy Snacks', '/images/cereal_bar.png'),
    ('C4', 'Rice Cakes', 1.50, 10, 'Healthy Snacks', '/images/rice_cakes.png')
    ON DUPLICATE KEY UPDATE id = VALUES(id);

-- Health Products (Row 4)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('D1', 'Protein Bar', 2.00, 15, 'Health Products', '/images/protein_bar.png'),
    ('D2', 'Energy Gel', 2.50, 10, 'Health Products', '/images/energy_gel.png'),
    ('D3', 'Vitamin Gummies', 2.20, 12, 'Health Products', '/images/vitamin_gummies.png'),
    ('D4', 'Energy Shot', 2.50, 8, 'Health Products', '/images/energy_shot.png')
    ON DUPLICATE KEY UPDATE id = VALUES(id);

-- Canned Drinks (Row 5)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('E1', 'Cola 330ml', 1.50, 12, 'Canned Drinks', '/images/cola_can.png'),
    ('E2', 'Lemonade 330ml', 1.50, 10, 'Canned Drinks', '/images/lemonade_can.png'),
    ('E3', 'Sparkling Water 330ml', 1.20, 10, 'Canned Drinks', '/images/sparkling_water_can.png'),
    ('E4', 'Energy Drink 250ml', 2.00, 10, 'Canned Drinks', '/images/energy_drink_can.png')
    ON DUPLICATE KEY UPDATE id = VALUES(id);

-- Bottled Drinks (Row 6)
INSERT INTO products (id, name, price, stock, category, image_url)
VALUES
    ('F1', 'Still Water 500ml', 1.00, 15, 'Bottled Drinks', '/images/still_water.png'),
    ('F2', 'Orange Juice 330ml', 1.80, 10, 'Bottled Drinks', '/images/orange_juice.png'),
    ('F3', 'Apple Juice 330ml', 1.80, 12, 'Bottled Drinks', '/images/apple_juice.png'),
    ('F4', 'Lemon Iced Tea 500ml', 1.80, 8, 'Bottled Drinks', '/images/lemon_iced_tea.png')
    ON DUPLICATE KEY UPDATE id = VALUES(id);



-- Inserting Admin User Account
INSERT INTO users (username, password, email, role)
VALUES ('admin', '$2b$10$rFzxvg4dckIixTQZQ7NPKufuomGwGCHTGTlJlaoR/a04Ofw7vpwgy', 'admin1@admin.com', 'ADMIN')
ON DUPLICATE KEY UPDATE username = username;

-- Ensuring auto_stock_enabled always has a value, never NULL (to prevent errors)
UPDATE products SET auto_stock_enabled = FALSE WHERE auto_stock_enabled IS NULL;


