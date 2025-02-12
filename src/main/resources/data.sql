INSERT INTO products (id, name, price, stock)
VALUES ('A1', 'Item 1', 1.50, 10)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('A2', 'Item 2', 2.00, 8)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('A3', 'Item 3', 1.25, 15)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('A4', 'Item 4', 2.50, 12)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('B1', 'Item 5', 1.00, 20)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('B2', 'Item 6', 1.75, 18)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('B3', 'Item 7', 2.25, 14)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('B4', 'Item 8', 1.50, 10)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('C1', 'Item 9', 1.25, 22)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('C2', 'Item 10', 2.75, 16)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('C3', 'Item 11', 5.50, 6)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('C4', 'Item 12', 1.00, 30)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('D1', 'Item 13', 2.50, 12)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('D2', 'Item 14', 3.00, 9)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('D3', 'Item 15', 2.25, 11)
    ON DUPLICATE KEY UPDATE id = id;

INSERT INTO products (id, name, price, stock)
VALUES ('D4', 'Item 16', 1.75, 25)
    ON DUPLICATE KEY UPDATE id = id;
