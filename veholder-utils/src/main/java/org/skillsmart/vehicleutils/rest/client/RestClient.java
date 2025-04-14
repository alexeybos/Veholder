package org.skillsmart.vehicleutils.rest.client;

import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.dto.DriverDto;
import org.skillsmart.veholder.entity.dto.VehicleDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RestClient {

    private final WebClient webClient;
    private final AtomicReference<String> tokenHolder = new AtomicReference<>();
    private final String baseUrl;

    public RestClient(Environment env) {
        //this.webClient = WebClient.create(baseUrl);
        this.baseUrl = env.getProperty("main.service.url");
        assert baseUrl != null;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> getToken(String username, String pass) {
        return webClient.post()
                .uri("/api/auth/login")
                .bodyValue(Map.of("username", username, "password", pass))
                .retrieve()
                .bodyToMono(String.class)
                .map(token -> {
                    tokenHolder.set(token);
                    return token;
                })
                .timeout(Duration.ofSeconds(5));
    }

    public Mono<String> getEnterprises() {
        return Mono.defer(() -> {
            String authToken = tokenHolder.get();
            if (authToken == null) return Mono.error(new IllegalStateException("No token. Call getToken() first"));
            return webClient.get()
                    .uri("/api/enterprises")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                    .retrieve()
                    .bodyToMono(String.class);
        });
    }

    public Mono<List> getBrands() {
        return Mono.defer(() -> {
            String authToken = tokenHolder.get();
            if (authToken == null) return Mono.error(new IllegalStateException("No token. Call getToken() first"));
            return webClient.get()
                    .uri("/api/brands")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                    .retrieve()
                    .bodyToMono(List.class);
        });
    }

    public Mono<Integer> generateVehiclesAndDrivers(Long enterpriseId, int vehicleCnt, List<Map<String, Object>> brands) {
        AtomicInteger driversCnt = new AtomicInteger(0);
        return Mono.defer(() -> {
            String authToken = tokenHolder.get();
            if (authToken == null) return Mono.error(new IllegalStateException("No token. Call getToken() first"));
            return Flux.range(1, vehicleCnt)
                    .concatMap(i -> {
                        VehicleDTO vehicle = generateVehicle(enterpriseId, brands);
                        return webClient.post()
                                .uri("api/vehicles")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                                .bodyValue(vehicle)
                                .retrieve()
                                .bodyToMono(Long.class)
                                .flatMap(vehicleId -> {
                                    if (i % 10 == 0) {
                                        DriverDto driver = generateDriver(enterpriseId, vehicleId);
                                        return webClient.post()
                                                .uri("api/drivers")
                                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                                                .bodyValue(driver)
                                                .retrieve()
                                                .bodyToMono(Void.class)
                                                .doOnSuccess(v -> driversCnt.incrementAndGet());
                                    } else {
                                        return Mono.empty();
                                    }
                                });
                    }).then(Mono.fromCallable(driversCnt::get));
        });
    }

    private VehicleDTO generateVehicle(Long enterpriseId, List<Map<String, Object>> brands) {
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setBrandId(Long.parseLong(((Integer) brands.get((int) (Math.random() * (brands.size()))).get("id")).toString()));
        vehicle.setRegistrationNumber(generateNumber());
        vehicle.setPrice(20000 + (Math.random() * (25000000 - 20000)));
        vehicle.setMileage(100 + (int) (Math.random() * (200000 - 100)));
        vehicle.setInOrder(true);
        vehicle.setColor(getColor());
        vehicle.setEnterpriseId(enterpriseId);
        vehicle.setYearOfProduction(1955 + (int) (Math.random() * (2025 - 1955 + 1)));
        return vehicle;
    }

    private DriverDto generateDriver(Long enterpriseId, Long vehicleId) {
        DriverDto driverDto = new DriverDto();
        driverDto.setVehicleId(vehicleId);
        driverDto.setBirthDate(generateDate());
        driverDto.setActive(true);
        driverDto.setName(getName());
        driverDto.setSalary(Math.round((20000 + (Math.random() * (250000 - 20000))) * 100.0) / 100.0);
        driverDto.setEnterpriseId(enterpriseId);
        return driverDto;
    }

    private String generateNumber() {
        char[] letters = {'A', 'B', 'C', 'E', 'H', 'K', 'M', 'O', 'P', 'T', 'У', 'X'};
        String result = Character.toString(letters[(int) (Math.random() * (letters.length))]);
        result += String.format("%03d", 1 + (int) (Math.random() * 999));
        result += Character.toString(letters[(int) (Math.random() * (letters.length))]);
        result += Character.toString(letters[(int) (Math.random() * (letters.length))]);
        result += String.format("%03d", 1 + (int) (Math.random() * 199));
        return result;
    }

    private String getColor() {
        List<String> colors = new ArrayList<>();
        colors.add("Синий");
        colors.add("Красный");
        colors.add("Желтый");
        colors.add("Зеленый");
        colors.add("Белый");
        colors.add("Черный");
        colors.add("Серый");
        colors.add("Фиолетовый");
        colors.add("Веселый");
        return colors.get((int) (Math.random() * (colors.size())));
    }

    private String getName() {
        List<String> names = new ArrayList<>();
        names.add("Иванов");
        names.add("Петров");
        names.add("Сидоров");
        names.add("Борисов");
        names.add("Смирнов");
        names.add("Чернов");
        names.add("Зайцев");
        names.add("Краснов");
        names.add("Воробьев");
        names.add("Антонов");
        names.add("Обломов");
        names.add("Пушкин");
        return names.get((int) (Math.random() * (names.size())));
    }

    private LocalDate generateDate() {
        LocalDate startDate = LocalDate.of(1965, 1, 1);  // Начальная дата
        LocalDate endDate = LocalDate.of(2002, 12, 31);   // Конечная дата
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        long randomDays = new Random().nextLong(daysBetween + 1);
        return startDate.plusDays(randomDays);
    }
}
