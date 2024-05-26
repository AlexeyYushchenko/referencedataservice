--liquibase formatted sql

--changeset ayushchenko:1
CREATE TABLE IF NOT EXISTS country
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(2) UNIQUE   NOT NULL,
    name        VARCHAR(255) UNIQUE NOT NULL,
    is_active   BOOLEAN   DEFAULT true,
    created_at  TIMESTAMP DEFAULT NOW(),
    modified_at TIMESTAMP DEFAULT NOW(),
    created_by  VARCHAR(64),
    modified_by VARCHAR(64)
);

--changeset ayushchenko:2
CREATE TABLE IF NOT EXISTS currency
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(3) NOT NULL,
    name        VARCHAR(50),
    enabled     BOOLEAN,

    -- Auditing
    created_at  TIMESTAMP,
    modified_at TIMESTAMP,
    created_by  VARCHAR(64),
    modified_by VARCHAR(64)
);