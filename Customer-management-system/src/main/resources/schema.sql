
-- Create and use the database
CREATE DATABASE IF NOT EXISTS customer_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE customer_db;


CREATE TABLE IF NOT EXISTS country (
                                       id   BIGINT       NOT NULL AUTO_INCREMENT,
                                       name VARCHAR(100) NOT NULL,
    CONSTRAINT pk_country PRIMARY KEY (id),
    CONSTRAINT uq_country_name UNIQUE (name)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS city (
                                    id         BIGINT       NOT NULL AUTO_INCREMENT,
                                    name       VARCHAR(100) NOT NULL,
    country_id BIGINT,
    CONSTRAINT pk_city PRIMARY KEY (id),
    CONSTRAINT fk_city_country
    FOREIGN KEY (country_id) REFERENCES country (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_city_country ON city (country_id);



CREATE TABLE IF NOT EXISTS customer (
                                        id   BIGINT       NOT NULL AUTO_INCREMENT,
                                        name VARCHAR(150) NOT NULL,
    dob  DATE         NOT NULL,
    nic  VARCHAR(50)  NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (id),
    CONSTRAINT uq_customer_nic UNIQUE (nic)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_customer_name ON customer (name);
CREATE INDEX idx_customer_nic  ON customer (nic);



CREATE TABLE IF NOT EXISTS mobile_number (
                                             id          BIGINT      NOT NULL AUTO_INCREMENT,
                                             number      VARCHAR(20),
    customer_id BIGINT      NOT NULL,
    CONSTRAINT pk_mobile_number PRIMARY KEY (id),
    CONSTRAINT fk_mobile_customer
    FOREIGN KEY (customer_id) REFERENCES customer (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_mobile_customer ON mobile_number (customer_id);



CREATE TABLE IF NOT EXISTS address (
                                       id          BIGINT       NOT NULL AUTO_INCREMENT,
                                       line1       VARCHAR(255),
    line2       VARCHAR(255),
    city_id     BIGINT,
    country_id  BIGINT,
    customer_id BIGINT       NOT NULL,
    CONSTRAINT pk_address PRIMARY KEY (id),
    CONSTRAINT fk_address_city
    FOREIGN KEY (city_id) REFERENCES city (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
    CONSTRAINT fk_address_country
    FOREIGN KEY (country_id) REFERENCES country (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
    CONSTRAINT fk_address_customer
    FOREIGN KEY (customer_id) REFERENCES customer (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_address_customer ON address (customer_id);
CREATE INDEX idx_address_city     ON address (city_id);
CREATE INDEX idx_address_country  ON address (country_id);



CREATE TABLE IF NOT EXISTS customer_family (
                                               customer_id        BIGINT NOT NULL,
                                               family_customer_id BIGINT NOT NULL,
                                               CONSTRAINT pk_customer_family
                                               PRIMARY KEY (customer_id, family_customer_id),
    CONSTRAINT fk_family_customer
    FOREIGN KEY (customer_id) REFERENCES customer (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT fk_family_member
    FOREIGN KEY (family_customer_id) REFERENCES customer (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    -- Prevent self-reference at DB level
    CONSTRAINT chk_no_self_family
    CHECK (customer_id <> family_customer_id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_family_member ON customer_family (family_customer_id);