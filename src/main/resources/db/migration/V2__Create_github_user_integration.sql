CREATE TABLE github_user_integration
(
    id                    UUID PRIMARY KEY NOT NULL,
    username              VARCHAR(255)     NOT NULL,
    personal_access_token VARCHAR(255)     NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE,
    updated_at            TIMESTAMP WITHOUT TIME ZONE
);