CREATE TABLE github_repository
(
    id          UUID PRIMARY KEY,
    repo_id     BIGINT       NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    full_name   VARCHAR(255) NOT NULL,
    description TEXT,
    is_private  BOOLEAN      NOT NULL,
    html_url    VARCHAR(512) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    saved_at    TIMESTAMP    NOT NULL
);

ALTER TABLE github_repository
    ADD CONSTRAINT uc_github_repository_repo UNIQUE (repo_id);