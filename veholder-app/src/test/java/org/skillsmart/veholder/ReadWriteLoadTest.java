package org.skillsmart.veholder;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpDsl;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class ReadWriteLoadTest extends Simulation {

    private static final Random RANDOM = new Random();
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    public ReadWriteLoadTest() {
        // Простой feeder
        FeederBuilder.Batchable<String> feeder = csv("data/test_vals.csv").circular();

        // Один сценарий с явным чередованием
        ScenarioBuilder scenario = scenario("Simple Load Test")
                .forever().on(
                        exec(feed(feeder))
                                .exec(http("read-enterprises")
                                        .get("/api/enterprises")
                                        .header("Authorization", "Bearer ${token}")
                                        )
                                .pause(100, 300)
                                .exec(http("read-vehicles")
                                        .get("/api/enterprises/${enterpriseId}/vehicles")
                                        .queryParam("size", "20")
                                        .header("Authorization", "Bearer ${token}")
                                        )
                                .pause(100, 300)
                                .doIf(session -> RANDOM.nextDouble() < 0.2).then(  // 20% запись
                                        exec(session -> {
                                            int reqId = COUNTER.getAndIncrement();
                                            int mileage = 10000 + RANDOM.nextInt(200000);
                                            return session
                                                    .set("mileage", mileage);
                                        })
                                                .exec(http("write-vehicle")
                                                        .put("/api/vehicles/${vehicleId}")
                                                        .header("Content-Type", "application/json")
                                                        .header("Authorization", "Bearer ${token}")
                                                        .body(StringBody("{\"mileage\": #{mileage}}"))
                                                        )
                                                .pause(1000, 2000)
                                )
                );

        setUp(
                scenario.injectOpen(
                        rampUsersPerSec(0).to(20).during(60),
                        constantUsersPerSec(20).during(300)
                )
        )
                .protocols(HttpDsl.http.baseUrl("http://localhost:8080"))
                .maxDuration(Duration.ofSeconds(400));
    }
}