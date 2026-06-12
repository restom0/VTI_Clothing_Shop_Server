-- Dummy PostgreSQL init data for Docker.
-- This file runs only when the Postgres data volume is created for the first time.

CREATE TABLE IF NOT EXISTS category
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    name VARCHAR
(
    255
) NOT NULL UNIQUE,
    description VARCHAR
(
    255
)
    );

CREATE TABLE IF NOT EXISTS brand
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    name VARCHAR
(
    255
) NOT NULL UNIQUE,
    description VARCHAR
(
    255
)
    );

CREATE TABLE IF NOT EXISTS product
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    name VARCHAR
(
    255
) NOT NULL UNIQUE,
    short_description VARCHAR
(
    255
),
    category_id BIGINT REFERENCES category
(
    id
),
    brand_id BIGINT REFERENCES brand
(
    id
)
    );

CREATE TABLE IF NOT EXISTS color
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    code VARCHAR
(
    255
) NOT NULL,
    name VARCHAR
(
    255
) NOT NULL,
    category_id BIGINT REFERENCES category
(
    id
)
    );

CREATE UNIQUE INDEX IF NOT EXISTS ux_color_code_category ON color (code, category_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_color_name_category ON color (name, category_id);

CREATE TABLE IF NOT EXISTS size
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    name VARCHAR
(
    255
) NOT NULL,
    height VARCHAR
(
    255
) NOT NULL,
    weight VARCHAR
(
    255
) NOT NULL,
    category_id BIGINT REFERENCES category
(
    id
)
    );

CREATE UNIQUE INDEX IF NOT EXISTS ux_size_name_category ON size (name, category_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_size_height_category ON size (height, category_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_size_weight_category ON size (weight, category_id);

CREATE TABLE IF NOT EXISTS material
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    name VARCHAR
(
    255
) NOT NULL,
    category_id BIGINT REFERENCES category
(
    id
)
    );

CREATE UNIQUE INDEX IF NOT EXISTS ux_material_name_category ON material (name, category_id);

CREATE TABLE IF NOT EXISTS imported_product
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    product_id BIGINT REFERENCES product
(
    id
),
    size_id BIGINT REFERENCES size
(
    id
),
    color_id BIGINT REFERENCES color
(
    id
),
    material_id BIGINT REFERENCES material
(
    id
),
    image_url VARCHAR
(
    255
),
    slider_url1 VARCHAR
(
    255
),
    slider_url2 VARCHAR
(
    255
),
    slider_url3 VARCHAR
(
    255
),
    slider_url4 VARCHAR
(
    255
),
    public_id_url VARCHAR
(
    255
),
    public_id_slider_url1 VARCHAR
(
    255
),
    public_id_slider_url2 VARCHAR
(
    255
),
    public_id_slider_url3 VARCHAR
(
    255
),
    public_id_slider_url4 VARCHAR
(
    255
),
    sku VARCHAR
(
    255
),
    gender VARCHAR
(
    255
),
    import_price INTEGER NOT NULL,
    import_number INTEGER NOT NULL,
    stock INTEGER NOT NULL
    );

CREATE TABLE IF NOT EXISTS input_sale
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    filter VARCHAR
(
    255
),
    filter_id BIGINT,
    sale_percentage REAL NOT NULL CHECK
(
    sale_percentage
    >=
    0
),
    discount REAL NOT NULL CHECK
(
    discount
    >=
    0
    AND
    discount
    <=
    100
),
    start_date DATE NOT NULL,
    end_date DATE
    );

CREATE TABLE IF NOT EXISTS on_sale_product
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    product_id BIGINT REFERENCES imported_product
(
    id
),
    input_sale_id BIGINT REFERENCES input_sale
(
    id
)
    );

CREATE TABLE IF NOT EXISTS voucher
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    code VARCHAR
(
    255
) NOT NULL UNIQUE,
    available_date BIGINT NOT NULL,
    end_date DATE NOT NULL,
    input_stock INTEGER NOT NULL,
    stock INTEGER NOT NULL,
    voucher_value REAL NOT NULL
    );

CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    created_at
    TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at BIGINT,
    version BIGINT,
    name VARCHAR
(
    255
) NOT NULL,
    username VARCHAR
(
    255
) NOT NULL UNIQUE,
    password VARCHAR
(
    255
) NOT NULL,
    birthday DATE,
    avatar_url VARCHAR
(
    255
),
    public_id_avatar_url VARCHAR
(
    255
),
    email VARCHAR
(
    255
) UNIQUE,
    phone_number VARCHAR
(
    255
) NOT NULL UNIQUE,
    role VARCHAR
(
    255
),
    address VARCHAR
(
    255
),
    salt VARCHAR
(
    255
) NOT NULL,
    gender VARCHAR
(
    255
)
    );

INSERT INTO category (id, created_at, updated_at, version, name, description)
VALUES (1, NOW(), NOW(), 0, 'T-Shirt', 'Everyday tops'),
       (2, NOW(), NOW(), 0, 'Jacket', 'Outerwear'),
       (3, NOW(), NOW(), 0, 'Pants', 'Bottoms') ON CONFLICT DO NOTHING;

INSERT INTO brand (id, created_at, updated_at, version, name, description)
VALUES (1, NOW(), NOW(), 0, 'VTI Basics', 'In-house essentials'),
       (2, NOW(), NOW(), 0, 'Urban Stitch', 'Casual streetwear') ON CONFLICT DO NOTHING;

INSERT INTO product (id, created_at, updated_at, version, name, short_description, category_id, brand_id)
VALUES (1, NOW(), NOW(), 0, 'Basic Cotton T-Shirt', 'Soft cotton crew neck', 1, 1),
       (2, NOW(), NOW(), 0, 'Denim Jacket', 'Classic denim outerwear', 2, 2) ON CONFLICT DO NOTHING;

INSERT INTO color (id, created_at, updated_at, version, code, name, category_id)
VALUES (1, NOW(), NOW(), 0, '#FFFFFF', 'White', 1),
       (2, NOW(), NOW(), 0, '#111111', 'Black', 1),
       (3, NOW(), NOW(), 0, '#2F5DA8', 'Denim Blue', 2) ON CONFLICT DO NOTHING;

INSERT INTO size (id, created_at, updated_at, version, name, height, weight, category_id)
VALUES (1, NOW(), NOW(), 0, 'M', '165-175cm', '55-70kg', 1),
       (2, NOW(), NOW(), 0, 'L', '175-185cm', '70-85kg', 1),
       (3, NOW(), NOW(), 0, 'M', '165-175cm', '55-75kg', 2) ON CONFLICT DO NOTHING;

INSERT INTO material (id, created_at, updated_at, version, name, category_id)
VALUES (1, NOW(), NOW(), 0, 'Cotton', 1),
       (2, NOW(), NOW(), 0, 'Denim', 2) ON CONFLICT DO NOTHING;

INSERT INTO imported_product (id, created_at, updated_at, version, product_id, size_id, color_id, material_id,
                              image_url, slider_url1, slider_url2, slider_url3, slider_url4,
                              public_id_url, public_id_slider_url1, public_id_slider_url2, public_id_slider_url3,
                              public_id_slider_url4,
                              sku, gender, import_price, import_number, stock)
VALUES (1, NOW(), NOW(), 0, 1, 1, 1, 1,
        'https://example.com/images/basic-cotton-tshirt-white.jpg',
        'https://example.com/images/basic-cotton-tshirt-white-1.jpg',
        'https://example.com/images/basic-cotton-tshirt-white-2.jpg',
        'https://example.com/images/basic-cotton-tshirt-white-3.jpg',
        'https://example.com/images/basic-cotton-tshirt-white-4.jpg',
        'dummy/basic-cotton-tshirt-white',
        'dummy/basic-cotton-tshirt-white-1',
        'dummy/basic-cotton-tshirt-white-2',
        'dummy/basic-cotton-tshirt-white-3',
        'dummy/basic-cotton-tshirt-white-4',
        'TSHIRT-WHT-M',
        'UNISEX',
        150000,
        50,
        50),
       (2, NOW(), NOW(), 0, 2, 3, 3, 2,
        'https://example.com/images/denim-jacket-blue.jpg',
        'https://example.com/images/denim-jacket-blue-1.jpg',
        'https://example.com/images/denim-jacket-blue-2.jpg',
        'https://example.com/images/denim-jacket-blue-3.jpg',
        'https://example.com/images/denim-jacket-blue-4.jpg',
        'dummy/denim-jacket-blue',
        'dummy/denim-jacket-blue-1',
        'dummy/denim-jacket-blue-2',
        'dummy/denim-jacket-blue-3',
        'dummy/denim-jacket-blue-4',
        'JACKET-DENIM-M',
        'UNISEX',
        450000,
        25,
        25) ON CONFLICT DO NOTHING;

INSERT INTO input_sale (id, created_at, updated_at, version, filter, filter_id, sale_percentage, discount, start_date,
                        end_date)
VALUES (1, NOW(), NOW(), 0, 'ALL', 0, 120, 10, CURRENT_DATE - 1, CURRENT_DATE + 30) ON CONFLICT DO NOTHING;

INSERT INTO on_sale_product (id, created_at, updated_at, version, product_id, input_sale_id)
VALUES (1, NOW(), NOW(), 0, 1, 1),
       (2, NOW(), NOW(), 0, 2, 1) ON CONFLICT DO NOTHING;

INSERT INTO voucher (id, created_at, updated_at, version, code, available_date, end_date, input_stock, stock,
                     voucher_value)
VALUES (1,
        NOW(),
        NOW(),
        0,
        'WELCOME10',
        (EXTRACT(EPOCH FROM (NOW() - INTERVAL '1 day')) * 1000)::BIGINT,
        (EXTRACT(EPOCH FROM (NOW() + INTERVAL '30 days')) * 1000)::BIGINT,
        100,
        100,
        10) ON CONFLICT DO NOTHING;

INSERT INTO users (id, created_at, updated_at, version, name, username, password, birthday,
                   email, phone_number, role, address, salt, gender)
VALUES (1,
        NOW(),
        NOW(),
        0,
        'Admin',
        'admin',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        DATE '1995-01-01',
        'admin@example.com',
        '0900000001',
        'ADMIN',
        'Ha Noi',
        'dummy-admin-salt',
        'MALE'),
       (2,
        NOW(),
        NOW(),
        0,
        'Demo User',
        'demo',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        DATE '1998-01-01',
        'demo@example.com',
        '0900000002',
        'USER',
        'Ho Chi Minh City',
        'dummy-demo-salt',
        'FEMALE') ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('category', 'id'), COALESCE((SELECT MAX(id) FROM category), 1), true);
SELECT setval(pg_get_serial_sequence('brand', 'id'), COALESCE((SELECT MAX(id) FROM brand), 1), true);
SELECT setval(pg_get_serial_sequence('product', 'id'), COALESCE((SELECT MAX(id) FROM product), 1), true);
SELECT setval(pg_get_serial_sequence('color', 'id'), COALESCE((SELECT MAX(id) FROM color), 1), true);
SELECT setval(pg_get_serial_sequence('size', 'id'), COALESCE((SELECT MAX(id) FROM size), 1), true);
SELECT setval(pg_get_serial_sequence('material', 'id'), COALESCE((SELECT MAX(id) FROM material), 1), true);
SELECT setval(pg_get_serial_sequence('imported_product', 'id'), COALESCE((SELECT MAX(id) FROM imported_product), 1),
              true);
SELECT setval(pg_get_serial_sequence('input_sale', 'id'), COALESCE((SELECT MAX(id) FROM input_sale), 1), true);
SELECT setval(pg_get_serial_sequence('on_sale_product', 'id'), COALESCE((SELECT MAX(id) FROM on_sale_product), 1),
              true);
SELECT setval(pg_get_serial_sequence('voucher', 'id'), COALESCE((SELECT MAX(id) FROM voucher), 1), true);
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) FROM users), 1), true);
