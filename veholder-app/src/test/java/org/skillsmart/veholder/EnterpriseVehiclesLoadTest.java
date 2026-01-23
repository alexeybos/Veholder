package org.skillsmart.veholder;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.OpenInjectionStep.RampRate.RampRateOpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class EnterpriseVehiclesLoadTest extends Simulation {

    public EnterpriseVehiclesLoadTest() {
        setUp(buildGetScenario()
                .injectOpen(injection())
                .protocols(setupProtocol())).assertions(global().responseTime()
                .max()
                .lte(10000), global().successfulRequests()
                .percent()
                .gt(90d));
                //.maxDuration(Duration.ofSeconds(600));
    }

    private static ScenarioBuilder buildGetScenario() {
        FeederBuilder.Batchable<String> enterpriseFeeder = csv("data/test_vals.csv").random();
        return CoreDsl.scenario("Load Get Test")
                .feed(enterpriseFeeder)
/*                .exec(http("get-enterprises")
                        .get("/api/enterprises")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer ${token}")
                )*/
                .exec(http("get-vehicles")
                        .get("/api/enterprises/${enterpriseId}/vehicles")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer ${token}"))
                //.pause(100, 300)
                /*.exec(http("get-trips")
                        .get("/api/trips/info/${vehicleId}")
                        .queryParam("start", "${data1}")
                        .queryParam("end", "${data2}")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer ${token}"))*/;
                //.pause(100, 300);
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
