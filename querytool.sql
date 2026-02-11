
DROP TABLE IF EXISTS activity_log CASCADE;
DROP TABLE IF EXISTS notes CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,  -- Plain text password
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'MANAGER', 'EDITOR')),
    created_at TIMESTAMP DEFAULT NOW(),
    last_login TIMESTAMP
);

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    deal_info INTEGER CHECK (deal_info BETWEEN 1 AND 4),
    price NUMERIC(10, 2) DEFAULT 0,
    note TEXT,  -- Optional notes field
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    category_id INTEGER REFERENCES categories(id),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE notes (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id),
    note_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE activity_log (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    action_type VARCHAR(50) NOT NULL,  -- 'ADD_CLIENT', 'DELETE_CLIENT', 'ADD_TASK', 'DELETE_TASK'
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO categories (name) VALUES 
('Software Development'),
('Consulting'),
('Marketing'),
('Support');

INSERT INTO users (username, password, role) VALUES 
('asanali', 'admin123', 'ADMIN'),
('ayim', 'manager123', 'MANAGER'),
('damir', 'editor123', 'EDITOR');
