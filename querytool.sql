DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS categories CASCADE;

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category_id INTEGER REFERENCES categories(id)
);

CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    deal_info INTEGER CHECK (deal_info BETWEEN 1 AND 4),
    price NUMERIC(10, 2) DEFAULT 0,
    task_id INTEGER REFERENCES tasks(id) ON DELETE SET NULL
);

INSERT INTO categories (name) VALUES 
('Software Development'),
('Consulting'),
('Marketing'),
('Support');


