# Telemetry

## Описание

Проект "Telemetry" представляет собой приложение для мониторинга данных, поступающих с датчиков (сенсоров) в умном доме.

## Структура проекта

Проект организован в соответствии со стандартной структурой Maven/Gradle проекта:

- **Основной класс приложения** аннотирован `@SpringBootApplication`.
- **Пакеты**:
    - `controller` - контроллеры для обработки HTTP-запросов.
    - `service` - сервисы для бизнес-логики.
    - `repository` - репозитории для доступа к базе данных.
    - `entity` - сущности JPA.
    - `dto` - объекты передачи данных (DTO).

## Зависимости

Проект использует следующие зависимости:

- Java 17
- Maven >=3.8.1 && <4.0.0
- `spring-boot-starter-web` - для создания веб-приложений.
- `spring-boot-starter-data-jpa` - для работы с JPA и базой данных.
- `postgresql` - драйвер для работы с PostgreSQL.
- `springfox-boot-starter` - для интеграции Swagger.
- `lombok` - для упрощения написания кода.
- `spring-boot-starter-test`, `spring-boot-testcontainers`, `junit-jupiter`, `testcontainers` - для тестирования.

