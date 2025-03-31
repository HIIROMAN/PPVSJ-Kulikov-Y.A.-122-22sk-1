-- db.sql
CREATE TABLE apartment (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           title VARCHAR(255),
                           url VARCHAR(255),
                           price_uah DOUBLE,
                           price_usd DOUBLE,
                           date_posted VARCHAR(100)
);