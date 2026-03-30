# Veholder
[![Java](https://img.shields.io/badge/Java-21+-green.svg)](https://www.java.com/)
[![SpringBoot](https://img.shields.io/badge/Spring%20Boot-3.4.3-blue.svg)](https://spring.io/)
[![Posgresql](https://img.shields.io/badge/PostgreSQL-12.22-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-orange.svg)](https://www.docker.com/)
[![Kafka](https://img.shields.io/badge/Apache-Kafka-black.svg)](https://kafka.apache.org/)
[![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-red.svg)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-Visualization-orange.svg)](https://grafana.com/)
[![Swagger](https://img.shields.io/badge/Swagger-API%20Docs-gree.svg)](https://swagger.io/)
***
Система управления автопарком в предприятиях.
Позволяет управлять списком предприятий, менеджеров и машин, отслеживать перемещение транспорта и получать аналитические отчеты.
Проект построен на базе классической многослойной (трехуровневой) архитектуры и рассчитан на обработку больших объемов данных о перемещениях автомобилей.

## Содержание
- [Основные возможности](#основные-возможности)
- [Технологический стек](#технологический-стек)
- [Архитектура](#архитектура-и-производительность)
- [Скриншоты](#скриншоты)
- [API Docs](#API-Docs)
- [Deployment](#deployment)
- [Docker](#docker)

## Основные возможности
- Управление предприятиями, автомобилями и водителями.
- Разграничение доступа менеджеров к данным по предприятиям.
- Загрузка/выгрузка поездок в формате GPX, визуализация маршрутов на карте.
- Генерация отчетов по пробегу автомобилей и занятости водителей по выбранным периодам.
- Возможность гибкого добавления иных аналитических отчетов в систему. 
- Поддержка экспорта/импорта данных в форматах JSON и CSV по всем сущностям.
- Отдельный микросервис для оповещения менеджеров об изменениях в их автопарке.

## Технологический стек
- Core: Java 21, Spring Boot 3.4.3
- БД: PostgreSQL
- Geo-сервисы: LeafLet (визуализация), Yandex Geocoder 
- Мониторинг: Prometheus + Grafana (настройка алертов через Alertmanager на Email/Telegram)
- Interface: REST API, Thymeleaf, Bootstrap, Telegram-bot (для быстрого получения основной информации)

## Архитектура и производительность
Проект реализован в трехуровневой архитектуре (Data, Business, API Layers) с вынесением логики уведомлений в микросервис.
Проведено нагрузочное тестирование (Read: 2 krps, Write: 1.5 krps)

## API Docs
Документация на API автоматически генерируется Swagger. После запуска приложения доступна по пути http://localhost:8080/swagger-ui/index.html
### Основные endpoints
```
GET|POST|PUT api/enterprises - работа с предприятиями

GET|POST|PUT api/vehicles - работа с автомобилями

GET|POST|PUT api/drivers - работа с водителями

GET|POST api/reports - генерация отчетов

GET|POST api/tracks - работа с треками автомобилей

GET|POST api/trips - работа с поездками автомобилей
```

## Deployment
### Запуск деплоя
Для автоматического развертывания используйте скрипт (запуск из основного каталога приложения):
```
chmod +x deploy.sh
./deploy.sh
```
### Основные хосты в рамках приложения
| Сервис           | Адрес | Описание                           |
|:-----------------| :--- |:-----------------------------------|
| **App**          | [localhost:8080](http://localhost:8080) | Основной интерфейс и логин         |
| **Swagger**      | [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) | Интерактивная документация API     |
| **Grafana**      | [localhost:3000](http://localhost:3000) | Дашборды мониторинга (admin/admin) |
| **Prometheus**   | [localhost:9090](http://localhost:9090) | База данных метрик                 |
| **Alertmanager** | [localhost:9093](http://localhost:9093) | Панель управления алертами         |

📲 Telegram bot: @VeholderMainBot.

## Скриншоты
##### Главный экран. Список предприятий и автомобилей.
![Главный экран. Список предприятий и автомобилей.](img/t26-03.JPG)
##### Поездки автомобиля за период.
![Поездки автомобиля за период.](img/t21-2.JPG)
##### Аналитичекие отчеты по автомобилям
![Аналитичекие отчеты по автомобилям](img/t23-9.JPG)
##### Загрузка треков
![Загрузка треков](img/t24-2.JPG)
##### REST выдача
![REST выдача](img/t44-1.JPG)
##### Swagger Docs
![Swagger Docs](img/t42-01.JPG)
##### Мониторинг (Grafana)
![Grafana](img/t43-04.JPG)
##### Telegram bot
![Telegram bot](img/telegram.jpg)

## Docker
Образ системы для тестового развертывания доступен на Docker Hub:
- App: docker pull alexeybos/veholder:latest
- DB: docker pull alexeybos/veholder-db:latest

Демонстрационные (тестовые) пользователи:

|Логин|Пароль|
|:-----------------|:---|
|man1|1111|
|man3|1111|

#Java #SpringBoot #PostgreSQL #GPX #GIS #LeafLet #Prometheus #Grafana #Microservices #HighLoad #Geocoder #RestAPI #Swagger #Thymeleaf
