--liquibase formatted sql

--changeset ayushchenko:1
CREATE TABLE IF NOT EXISTS country_localization
(
    country_id     INT REFERENCES country (id) ON DELETE CASCADE,
    language_code  VARCHAR(50),
    localized_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (country_id, language_code)
);

--changeset ayushchenko:2
CREATE TABLE IF NOT EXISTS currency_localization
(
    currency_id     INT REFERENCES currency (id) ON DELETE CASCADE,
    language_code  VARCHAR(50),
    localized_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (currency_id, language_code)
);