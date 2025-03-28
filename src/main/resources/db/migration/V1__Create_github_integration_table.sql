CREATE TABLE github_integration
(
    id                UUID PRIMARY KEY NOT NULL,
    organization_name VARCHAR(255) NOT NULL,
    client_id         VARCHAR(255) NOT NULL,
    client_secret     VARCHAR(255) NOT NULL,
    access_token      VARCHAR(255),
    token_expires_at  TIMESTAMP,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP
);