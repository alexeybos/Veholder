package org.skillsmart.veholder;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpDsl;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class ReadWriteSeparatedTest extends Simulation {

    private static final Random RANDOM = new Random();
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW4zIiwiaWF0IjoxNzY1Mzc0MjU5LCJleHAiOjE3NjU0NjA2NTl9.3klap0xG0SAV8trxBBvT_9eC6bgIxu2KkpFaHdRR6_c";

    // Конфигурация
    private static final int TARGET_RPS = 500;
    private static final double WRITE_PROBABILITY = 0.3; // 20% записи

    public ReadWriteSeparatedTest() {
        FeederBuilder<String> feeder = csv("data/test_vals.csv").circular();

        ScenarioBuilder scenario = scenario("Separated Read/Write Test")
                .during(Duration.ofMinutes(5)).on(
                        exec(feed(feeder))
                                .exec(session -> session.set("authHeader", "Bearer ${token}"))

                                // --- ОПЕРАЦИИ ЧТЕНИЯ ---
                                // 1. Чтение предприятий
                                .exec(http("READ-get-enterprises")
                                        .get("/api/enterprises")
                                        .header("Authorization", "#{authHeader}")
                                        )
                                .pause(10, 30)

                                // 2. Чтение транспортных средств
                                .exec(http("READ-get-vehicles")
                                        .get("/api/enterprises/${enterpriseId}/vehicles")
                                        .queryParam("size", "20")
                                        .header("Authorization", "#{authHeader}")
                                        )
                                .pause(10, 30)

                                // 3. Чтение информации о поездках
                                .exec(http("READ-get-trips")
                                        .get("/api/trips/info/${vehicleId}")
                                        .queryParam("start", "${data1}")
                                        .queryParam("end", "${data2}")
                                        .header("Authorization", "#{authHeader}")
                                        )
                                .pause(10, 30)

                                // --- ОПЕРАЦИИ ЗАПИСИ (20% случаев) ---
                                .doIf(session -> RANDOM.nextDouble() < WRITE_PROBABILITY).then(
                                        exec(session -> {
                                            int mileage = 10000 + RANDOM.nextInt(200000);
                                            String vehicleId = session.getString("vehicleId");
                                            if (vehicleId == null || vehicleId.isEmpty()) {
                                                vehicleId = String.valueOf(1000 + (COUNTER.getAndIncrement() % 9000));
                                            }
                                            return session
                                                    .set("writeVehicleId", vehicleId)
                                                    .set("mileage", mileage);
                                        })
                                                .exec(http("WRITE-update-vehicle")
                                                        .put("/api/vehicles/#{writeVehicleId}")
                                                        .header("Content-Type", "application/json")
                                                        .header("Authorization", "#{authHeader}")
                                                        .body(StringBody("{\"mileage\": #{mileage}}"))
                                                        )
                                                .pause(1000, 2000)
                                )
                );

        setUp(
                scenario.injectOpen(
                        rampUsersPerSec(0).to(TARGET_RPS).during(240),
                        constantUsersPerSec(TARGET_RPS).during(60),
                        rampUsersPerSec(TARGET_RPS).to(0).during(60)
                )
        )
                .protocols(HttpDsl.http.baseUrl("http://localhost:8080"))
                .maxDuration(Duration.ofMinutes(6));
    }
}
