# 🏰 Hogwarts School Management System

Проект "Hogwarts School" - это полнофункциональное Spring Boot приложение для управления студентами и факультетами школы магии Хогвартс.

## 🚀 Технологический стек

### Backend
- **Java 17** - основной язык разработки
- **Spring Boot 3.2.2** - основной фреймворк
- **Spring Data JPA** - работа с базой данных
- **Spring Web MVC** - REST API
- **Spring Doc OpenAPI** - документация API

### База данных
- **PostgreSQL** - основная production БД
- **H2 Database** - in-memory БД для тестирования
- **Liquibase** - управление миграциями базы данных

### Дополнительные технологии
- **Lombok** - уменьшение boilerplate кода
- **Logback** - логирование с ротацией логов
- **Gradle** - система сборки

## 🎯 Основные функции

### Управление студентами
- CRUD операции для студентов
- Поиск студентов по факультету
- Фильтрация и пагинация
- Загрузка аватаров студентов

### Управление факультетами
- CRUD операции для факультетов
- Поиск факультетов по цвету и названию
- Привязка студентов к факультетам

### Дополнительные возможности
- RESTful API с документацией Swagger
- Многопоточная обработка данных
- Загрузка файлов (аватары студентов)
- Actuator endpoints для мониторинга

## 🏗️ Архитектура проекта
src/
├── main/
│ ├── java/ru/hogwarts/school_2/
│ │ ├── controller/ # REST контроллеры
│ │ ├── service/ # бизнес-логика
│ │ ├── repository/ # Data JPA репозитории
│ │ ├── model/ # сущности БД
│ │ └── dto/ # Data Transfer Objects
│ └── resources/
│ ├── application.properties # общая конфигурация
│ ├── application-dev.properties # dev профиль
│ ├── application-prod.properties # production профиль
│ ├── application-test.properties # test профиль
│ └── liquibase/ # миграции БД
└── test/ # unit и integration tests

text

## 🔧 Настройка и запуск

### Требования
- Java 17+
- PostgreSQL 12+
- Gradle 7+

### Запуск с разными профилями

**Development (по умолчанию):**
```bash
./gradlew bootRun
# или
java -jar application.jar --spring.profiles.active=dev
Production:

bash
java -jar application.jar --spring.profiles.active=prod
Test:

bash
./gradlew test
Настройка базы данных
Создайте БД PostgreSQL:

sql
CREATE DATABASE hogwarts;
CREATE DATABASE hogwarts_prod;
Настройте подключение в application.properties

📊 API Документация
После запуска приложения доступна документация:

Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI JSON: http://localhost:8080/api-docs

🧪 Тестирование
Проект включает comprehensive тестирование:

Unit tests (JUnit 5, Mockito)

Integration tests

Тестовые профили с H2 database

Coverage отчеты

🛠️ Навыки разработчика
Данный проект демонстрирует следующие профессиональные навыки:

Backend Development
Разработка RESTful API с Spring Boot

Работа с Spring Data JPA и Hibernate

Реализация сложных SQL запросов

Оптимизация производительности БД

Архитектура и проектирование
Слоистая архитектура (Controller-Service-Repository)

DTO паттерн для API

Правильное разделение ответственности

Конфигурация через профили

Работа с базами данных
Миграции с Liquibase

Оптимизация запросов (индексы)

Работа с PostgreSQL и H2

Транзакционность и кэширование

Производственные практики
Логирование с ротацией файлов

Мониторинг через Spring Boot Actuator

Обработка ошибок и валидация

Конфигурация для разных environments

Инструменты и DevOps
Сборка с Gradle

Контейнеризация зависимостей

Настройка CI/CD готовности

Профилирование приложения

📈 Производительность
Оптимизированные SQL запросы с индексами

Пагинация для больших наборов данных

Кэширование часто используемых данных

Многопоточная обработка где необходимо

🔮 Планы по развитию
Docker контейнеризация

Kubernetes deployment

Redis кэширование

OAuth2 аутентификация

WebSocket уведомления

GraphQL API

📞 Контакты
Разработчик: Алексей Чудинов

Проект демонстрирует полный цикл разработки enterprise приложения на Spring Boot ecosystem.
