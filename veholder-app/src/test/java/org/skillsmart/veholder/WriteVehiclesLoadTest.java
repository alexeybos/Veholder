package org.skillsmart.veholder;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.OpenInjectionStep.RampRate.RampRateOpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class WriteVehiclesLoadTest extends Simulation {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private static final Random RANDOM = new Random();

    public WriteVehiclesLoadTest() {
        setUp(buildGetScenario()
                .injectOpen(injection())
                .protocols(setupProtocol())).assertions(global().responseTime()
                .max()
                .lte(10000), global().successfulRequests()
                .percent()
                .gt(90d));
                //.maxDuration(Duration.ofSeconds(400));
    }

    private static ScenarioBuilder buildGetScenario() {
        FeederBuilder.Batchable<String> enterpriseFeeder = csv("data/test_vals_write.csv").random();
        return CoreDsl.scenario("Load Get Test")
                .feed(enterpriseFeeder)
                .exec(session -> {
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
                );
                //.pause(1000, 2000);
    }

    private static HttpProtocolBuilder setupProtocol() {
        return HttpDsl.http.baseUrl("http://localhost:8080")
                .acceptHeader("application/json")
                .maxConnectionsPerHost(1000)
                .userAgentHeader("Performance Test")
                .shareConnections()
                .disableCaching();
    }

    private RampRateOpenInjectionStep injection() {
        int totalUsers = 5000;
        double userRampUpPerInterval = 10;
        double rampUpIntervalInSeconds = 30;

        int rampUptimeSeconds = 300;
        int duration = 300;
        return rampUsersPerSec(userRampUpPerInterval / (rampUpIntervalInSeconds)).to(totalUsers)
                .during(Duration.ofSeconds(rampUptimeSeconds + duration));
    }

}
