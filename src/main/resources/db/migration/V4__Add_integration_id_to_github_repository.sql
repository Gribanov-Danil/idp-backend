-- Шаг 1: Добавляем столбец без NOT NULL
ALTER TABLE github_repository
    ADD COLUMN integration_id UUID;

-- Шаг 2: Заполняем столбец значениями (например, используя значение по умолчанию)
UPDATE github_repository
SET integration_id = (SELECT id FROM github_user_integration LIMIT 1);

-- Шаг 3: Добавляем ограничение NOT NULL
ALTER TABLE github_repository
    ALTER COLUMN integration_id SET NOT NULL;

-- Шаг 4: Добавляем внешний ключ
ALTER TABLE github_repository
    ADD CONSTRAINT fk_github_repository_integration
        FOREIGN KEY (integration_id)
            REFERENCES github_user_integration(id);