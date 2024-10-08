DROP TABLE IF EXISTS stats CASCADE;

CREATE TABLE IF NOT EXISTS stats
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app VARCHAR(100) NOT NULL,
    uri VARCHAR(500) NOT NULL,
    ip VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE
);