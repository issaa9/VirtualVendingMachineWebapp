INSERT INTO products (id, name, price, stock, image_url)
VALUES ('A1', 'Cheese & Onion Crisps', 1.50, 10, 'images/crisp_cheese_onion.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('A2', 'Salt & Vinegar Crisps', 2.00, 8, 'images/crisp_salt_vinegar.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('A3', 'Ready Salted Crisps', 1.25, 15, 'images/crisp_ready_salted.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('A4', 'Prawn Cocktail Crisps', 2.50, 12, 'images/crisp_prawn_cocktail.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('B1', 'Chocolate Digestives', 1.00, 20, 'images/chocolate_digestives.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('B2', 'Shortbread Biscuits', 1.75, 18, 'images/shortbread_biscuits.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('B3', 'Mixed Nuts Packet', 2.25, 14, 'images/mixed_nuts_packet.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('B4', 'Dried Fruit Packet', 1.50, 10, 'images/dried_fruit_mix.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('C1', 'Still Water 500ml', 1.25, 22, 'images/still_water.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('C2', 'Sparkling Water 500ml', 2.75, 16, 'images/sparkling_water.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('C3', 'Orange Juice 330ml', 5.50, 6, 'images/orange_juice.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('C4', 'Apple Juice 330ml', 1.00, 30, 'images/apple_juice.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('D1', 'Cola 330ml', 2.50, 12, 'images/cola_can.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);;

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('D2', 'Lemonade 330ml', 3.00, 9, 'images/lemonade_can.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);;

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('D3', 'Energy Drink 250ml', 2.25, 11, 'images/energy_drink_can.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);;

INSERT INTO products (id, name, price, stock, image_url)
VALUES ('D4', 'Lemon Iced Tea', 1.75, 25, 'images/lemon_iced_tea.webp')
    ON DUPLICATE KEY UPDATE image_url = VALUES(image_url);;
