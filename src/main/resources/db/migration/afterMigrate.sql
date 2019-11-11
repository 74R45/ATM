CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DELETE FROM account;

INSERT INTO account (id, name)
VALUES (uuid_generate_v4(), 'Kolya');