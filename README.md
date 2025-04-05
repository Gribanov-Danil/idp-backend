# Project Hub Backend

## Описание
Проект представляет собой backend для управления Project Hub, построенный на Spring Boot.

## Запуск проекта
Для запуска проекта в режиме разработки выполните следующие шаги:

1. Убедитесь, что на вашем компьютере установлены Docker и Docker Compose.
2. Склонируйте репозиторий и перейдите в корневую директорию проекта.
3. Проверьте, что файл `.env` настроен корректно (пример настроек ниже):
   - POSTGRES_DB=idp_db
   - POSTGRES_USER=postgres
   - POSTGRES_PASSWORD=postgres
   - SPRING_PROFILES_ACTIVE=dev
   - SERVER_PORT=8080
   - TARGETPLATFORM=linux/arm64
   - DOCKER_BUILDKIT=1
   - COMPOSE_DOCKER_CLI_BUILD=1
4. Постройте и запустите контейнеры с помощью команды:
   ```bash
   docker-compose up --build
   ```
5. После успешного запуска приложение будет доступно по адресу: http://localhost:8080

## Сборка Docker образа вручную (опционально)
Для сборки образа с поддержкой мультиархитектурной сборки выполните следующую команду:

```bash
docker buildx build --platform linux/arm64,linux/amd64 -t project-hub-backend:latest .
```

## Структура проекта
- `Dockerfile` – инструкция для сборки Docker образа приложения.
- `docker-compose.yml` – конфигурация для создания и запуска контейнеров.
- `pom.xml` – конфигурация Maven проекта.
- `src/` – исходный код приложения.
