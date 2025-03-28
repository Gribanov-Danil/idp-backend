CREATE TABLE groups
(
    id                    UUID PRIMARY KEY,
    name                  VARCHAR(255) NOT NULL,
    type                  VARCHAR(50)  NOT NULL,
    personal_access_token VARCHAR(255) NOT NULL,
    external_id           VARCHAR(255) NOT NULL,
    is_imported           BOOLEAN      NOT NULL,
    created_at            TIMESTAMP,
    updated_at            TIMESTAMP
);