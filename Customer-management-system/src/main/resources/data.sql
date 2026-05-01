-- =============================================================================
-- Customer Management System — DML (Seed / Master Data)
-- Run AFTER ddl.sql
-- =============================================================================

USE customer_db;

-- =============================================================================
-- COUNTRIES
-- =============================================================================
INSERT INTO country (name) VALUES
                               ('Sri Lanka'),
                               ('India'),
                               ('United States'),
                               ('United Kingdom'),
                               ('Australia'),
                               ('Canada'),
                               ('Germany'),
                               ('France'),
                               ('Singapore'),
                               ('Japan')
    ON DUPLICATE KEY UPDATE name = VALUES(name);


-- =============================================================================
-- CITIES
-- =============================================================================
INSERT INTO city (name, country_id) VALUES
                                        -- Sri Lanka (id=1)
                                        ('Colombo',        1),
                                        ('Kandy',          1),
                                        ('Galle',          1),
                                        ('Jaffna',         1),
                                        ('Negombo',        1),
                                        ('Matara',         1),
                                        ('Kurunegala',     1),
                                        ('Anuradhapura',   1),
                                        ('Trincomalee',    1),
                                        ('Batticaloa',     1),

                                        -- India (id=2)
                                        ('New Delhi',      2),
                                        ('Mumbai',         2),
                                        ('Bangalore',      2),
                                        ('Chennai',        2),
                                        ('Kolkata',        2),
                                        ('Hyderabad',      2),
                                        ('Pune',           2),

                                        -- United States (id=3)
                                        ('New York',       3),
                                        ('Los Angeles',    3),
                                        ('Chicago',        3),
                                        ('Houston',        3),
                                        ('San Francisco',  3),

                                        -- United Kingdom (id=4)
                                        ('London',         4),
                                        ('Manchester',     4),
                                        ('Birmingham',     4),

                                        -- Australia (id=5)
                                        ('Sydney',         5),
                                        ('Melbourne',      5),
                                        ('Brisbane',       5),

                                        -- Canada (id=6)
                                        ('Toronto',        6),
                                        ('Vancouver',      6),
                                        ('Montreal',       6),

                                        -- Germany (id=7)
                                        ('Berlin',         7),
                                        ('Munich',         7),
                                        ('Hamburg',        7),

                                        -- France (id=8)
                                        ('Paris',          8),
                                        ('Lyon',           8),

                                        -- Singapore (id=9)
                                        ('Singapore',      9),

                                        -- Japan (id=10)
                                        ('Tokyo',          10),
                                        ('Osaka',          10)
    ON DUPLICATE KEY UPDATE name = VALUES(name);


-- =============================================================================
-- SAMPLE CUSTOMERS (optional demo data)
-- =============================================================================
INSERT INTO customer (name, dob, nic) VALUES
                                          ('Kamal Perera',     '1985-03-12', '198503100245'),
                                          ('Nimal Silva',      '1992-07-25', '199207251234'),
                                          ('Suresh Fernando',  '1978-11-08', '197811085678'),
                                          ('Priya Jayasinghe', '2000-04-30', '200004301111'),
                                          ('Chamari Wijesinghe', '1995-09-15', '199509152222')
    ON DUPLICATE KEY UPDATE name = VALUES(name);


-- =============================================================================
-- SAMPLE MOBILE NUMBERS
-- =============================================================================
INSERT INTO mobile_number (number, customer_id)
SELECT '0771234567', id FROM customer WHERE nic = '198503100245'
UNION ALL
SELECT '0112345678', id FROM customer WHERE nic = '198503100245'
UNION ALL
SELECT '0769876543', id FROM customer WHERE nic = '199207251234'
UNION ALL
SELECT '0754441111', id FROM customer WHERE nic = '197811085678';


-- =============================================================================
-- SAMPLE ADDRESSES
-- =============================================================================
INSERT INTO address (line1, line2, city_id, country_id, customer_id)
SELECT
    '42 Temple Road', 'Wellawatte',
    (SELECT id FROM city WHERE name = 'Colombo' LIMIT 1),
    (SELECT id FROM country WHERE name = 'Sri Lanka' LIMIT 1),
    (SELECT id FROM customer WHERE nic = '198503100245' LIMIT 1)
UNION ALL
SELECT
    '15 Kandy Road', NULL,
    (SELECT id FROM city WHERE name = 'Kandy' LIMIT 1),
    (SELECT id FROM country WHERE name = 'Sri Lanka' LIMIT 1),
    (SELECT id FROM customer WHERE nic = '199207251234' LIMIT 1);


-- =============================================================================
-- SAMPLE FAMILY RELATIONSHIPS
-- Kamal and Nimal are family members of each other
-- =============================================================================
INSERT IGNORE INTO customer_family (customer_id, family_customer_id)
SELECT
    (SELECT id FROM customer WHERE nic = '198503100245' LIMIT 1),
    (SELECT id FROM customer WHERE nic = '199207251234' LIMIT 1)
UNION ALL
SELECT
    (SELECT id FROM customer WHERE nic = '199207251234' LIMIT 1),
    (SELECT id FROM customer WHERE nic = '198503100245' LIMIT 1);