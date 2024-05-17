CREATE TABLE carts (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id)
);

CREATE TABLE cart_items (
    id SERIAL PRIMARY KEY,
    book_isbn VARCHAR(255) REFERENCES books(isbn),
    quantity INT,
    cart_id BIGINT REFERENCES carts(id)
);