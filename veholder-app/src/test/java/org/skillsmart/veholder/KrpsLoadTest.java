package org.skillsmart.veholder;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class KrpsLoadTest extends Simulation{

    // Конфигурация нагрузочного теста
    private static final int WARMUP_DURATION = 60; // Прогрев
    private static final int TEST_STAGES = 5; // Количество этапов нагрузки
    private static final int STAGE_DURATION = 60; // Длительность каждого этапа
    private static final int MAX_TARGET_RPS = 1000; // Максимальная целевая нагрузка (1 krps)

    // Счетчик для уникальных запросов
    private static final AtomicInteger requestCounter = new AtomicInteger(1);

    public KrpsLoadTest() {
        FeederBuilder.Batchable<String> enterpriseFeeder = csv("data/test_vals.csv").random();
        setUp(buildGetScenario()
                .injectOpen(
                        // Прогрев системы
                        rampUsersPerSec(0).to(50).during(WARMUP_DURATION),

                        // Постепенное увеличение нагрузки
                        rampUsersPerSec(50).to(MAX_TARGET_RPS / 3).during(STAGE_DURATION * 2),
                        constantUsersPerSec(MAX_TARGET_RPS / 3).during(STAGE_DURATION),

                        rampUsersPerSec(MAX_TARGET_RPS / 3).to(MAX_TARGET_RPS / 2).during(STAGE_DURATION),
                        constantUsersPerSec(MAX_TARGET_RPS / 2).during(STAGE_DURATION),

                        rampUsersPerSec(MAX_TARGET_RPS / 2).to(MAX_TARGET_RPS).during(STAGE_DURATION),
                        constantUsersPerSec(MAX_TARGET_RPS).during(STAGE_DURATION * 2),

                        // Сброс нагрузки для проверки восстановления
                        rampUsersPerSec(MAX_TARGET_RPS).to(50).during(STAGE_DURATION)
                )
                .protocols(highPerformanceProtocol()));
    }

    private static ScenarioBuilder buildGetScenario() {
        FeederBuilder.Batchable<String> enterpriseFeeder = csv("data/test_vals.csv").random();
        return CoreDsl.scenario("Load Get Test")
                .feed(enterpriseFeeder)
                .exec(http("get-enterprises")
                        .get("/api/enterprises")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer ${token}")
                )
                .exec(http("get-vehicles")
                        .get("/api/enterprises/${enterpriseId}/vehicles")
                        .queryParam("page", "0")
                        .queryParam("size", "3000")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer ${token}"))
                .exec(http("get-trips")
                        .get("/api/trips/info/${vehicleId}")
                        .queryParam("start", "${data1}")
                        .queryParam("end", "${data2}")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer ${token}"));
    }

    private static HttpProtocolBuilder highPerformanceProtocol() {
        return HttpDsl.http
                .baseUrl("http://localhost:8080")
                .acceptHeader("application/json")
                .userAgentHeader("Gatling-Performance-Test")

                // Высокопроизводительная конфигурация
                .shareConnections()
                .maxConnectionsPerHost(1000) // Увеличиваем количество соединений

                // Таймауты (убрали неверный метод perUserKeyManagerFactory)
                //.connectionTimeout(3000)
                //.handshakeTimeout(3000)
                //.requestTimeout(5000)

                // Оптимизации
                .disableCaching()
                .enableHttp2();
    }
}
