DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
  id SERIAL PRIMARY KEY,
  description VARCHAR(1024) NOT NULL,
  requestor_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
  id SERIAL PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  description VARCHAR(1024),
  is_available BOOLEAN NOT NULL,
  owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  request_id INTEGER REFERENCES requests(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bookings (
  id SERIAL PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  item_id INTEGER NOT NULL REFERENCES items(id) ON DELETE CASCADE,
  booker_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  status VARCHAR(24) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
  id SERIAL PRIMARY KEY,
  text VARCHAR(2048) NOT NULL,
  item_id INTEGER NOT NULL REFERENCES items(id) ON DELETE CASCADE,
  author_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Создание индексов для ускорения поиска
CREATE INDEX IF NOT EXISTS idx_items_owner_id ON items(owner_id);
CREATE INDEX IF NOT EXISTS idx_items_request_id ON items(request_id);
CREATE INDEX IF NOT EXISTS idx_bookings_item_id ON bookings(item_id);
CREATE INDEX IF NOT EXISTS idx_bookings_booker_id ON bookings(booker_id);
CREATE INDEX IF NOT EXISTS idx_comments_item_id ON comments(item_id);
CREATE INDEX IF NOT EXISTS idx_comments_author_id ON comments(author_id);
CREATE INDEX IF NOT EXISTS idx_requests_requestor_id ON requests(requestor_id);