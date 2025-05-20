package org.skillsmart.vehicleutils.rest.client;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
//import org.skillsmart.vehicleutils.util.GraphHopperTrackGenerator;
import org.skillsmart.vehicleutils.util.GraphHopperTrackGenerator;
import org.skillsmart.vehicleutils.util.entity.TrackPoint;
import org.skillsmart.veholder.entity.VehicleTrack;
import org.skillsmart.veholder.entity.dto.DriverDto;
import org.skillsmart.veholder.entity.dto.VehicleDTO;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.skillsmart.vehicleutils.util.GraphHopperTrackGenerator.generateTrack;

@Service
public class RestClient {

    private final WebClient webClient;
    private final AtomicReference<String> tokenHolder = new AtomicReference<>();
    private final String baseUrl;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    //private final GraphHopperTrackGenerator trackGenerator = new GraphHopperTrackGenerator();

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
                .bodyToMono(Map.class)
                .map(token -> {
                    tokenHolder.set((String) token.get("token"));
                    return (String) token.get("token");
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

    public Mono<Integer> generateVehicleTrack(Long vehicleId, double lon, double lat, int radius, int speed, int length) throws IOException {
        AtomicInteger pointsCnt = new AtomicInteger(0);
        //сначала непосредственно генерирование массива с треком, потом его передача в post для вставки (возможно пакетами?)
        List<Map<String, Object>> track = generateTrackByPoints(vehicleId, lon, lat, radius, speed, length);
        //List<VehicleTrack> track = generateTrackByPoints(vehicleId, lon, lat, radius, speed, length);
        final int packSize = 10;
        int packs = track.size() / packSize;
        return Mono.defer(() -> {
            String authToken = tokenHolder.get();
            if (authToken == null) return Mono.error(new IllegalStateException("No token. Call getToken() first"));
            return Flux.range(0, track.size())
                    .concatMap(i -> {
                        Map<String, Object> tPoint = track.get(i);
                        return webClient.post()
                                .uri("api/tracks/" + vehicleId + "/point")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                                .bodyValue(tPoint)
                                .retrieve()
                                .bodyToMono(Void.class)
                                .doOnSuccess(v -> {
                                    pointsCnt.incrementAndGet();
                                    System.out.println("Generation in progress. Next point (" + i + ") inserted.");
                                });
                    }).then(Mono.fromCallable(pointsCnt::get));
        });
    }

    private List<Map<String, Object>> generateTrackByPoints(Long vehicleId, double lon, double lat, int radius, int speed, int length) throws IOException {
    //private List<VehicleTrack> generateTrackByPoints(Long vehicleId, double lon, double lat, int radius, int speed, int length) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        Point center = geometryFactory.createPoint(new Coordinate(lon, lat));
        //получение точки конца маршрута
        double[] endPointLatLon = generateRandomPointInRadius(lat, lon, radius);
        Point endPoint = geometryFactory.createPoint(new Coordinate(endPointLatLon[1], endPointLatLon[0]));
        //логика создания трека
        // Задаем точки маршрута (старт, промежуточные, финиш)
        List<String> points = List.of(
                "53.224967,50.198440", // Берлин, Бранденбургские ворота
                //"52.5185,13.4081",     // Промежуточная точка
                "53.192163,50.134063"      // Конечная точка
        );
        /*List<String> points = List.of(
                "53.246587,50.215019", // Берлин, Бранденбургские ворота
                //"52.5185,13.4081",     // Промежуточная точка
                "53.212922,50.180384"      // Конечная точка
        );*/
        //53.224967, 50.198440
        //53.192163, 50.134063

        // Генерируем трек
        List<TrackPoint> track = generateTrack(points, 10); // 10 сек между точками
        System.out.println("[DEBUG] track.size() = " + track.size());
        List<VehicleTrack> adjustedTrack = new ArrayList<>();
        /*for (TrackPoint value : track) {
            //result.add("", track.get(i))
            VehicleTrack trackPoint = new VehicleTrack();
            Point point = geometryFactory.createPoint(new Coordinate(value.getLon(), value.getLat()));
            point.setSRID(4326);
            trackPoint.setPoint(point);
            trackPoint.setVehicleId(vehicleId);
            trackPoint.setRecordedAt(value.getTime());
            adjustedTrack.add(trackPoint);
        }*/

        for (TrackPoint value : track) {
            Map<String, Object> tPoint = new HashMap<>();
            tPoint.put("lon", value.getLon());
            tPoint.put("lat", value.getLat());
            tPoint.put("recordedAt", value.getTime().toString());
            result.add(tPoint);
        }

        return result;
    }

    /**
     * Генерирует случайную точку в заданном радиусе от центра.
     * @param centerLat Широта центра в градусах.
     * @param centerLon Долгота центра в градусах.
     * @param radiusKm Радиус в километрах.
     * @return Массив из двух элементов: [широта, долгота].
     */
    private static double[] generateRandomPointInRadius(double centerLat, double centerLon, double radiusKm) {
        Random random = new Random();

        // 1. Генерируем случайное направление (0..2π) и расстояние (0..radius)
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * radiusKm;

        // 2. Конвертируем расстояние из км в градусы (примерно)
        double distanceInDegrees = distance / 111.32; // 1° ≈ 111.32 км

        // 3. Вычисляем новые координаты
        double newLat = centerLat + distanceInDegrees * Math.cos(angle);
        double newLon = centerLon + distanceInDegrees * Math.sin(angle) / Math.cos(Math.toRadians(centerLat));

        return new double[]{newLat, newLon};
    }

    /*public Mono<Integer> generateVehicleTrack(Long vehicleId, double lon, double lat, int radius, int speed, int length) throws IOException {
        AtomicInteger pointsCnt = new AtomicInteger(0);
        //сначала непосредственно генерирование массива с треком, потом его передача в post для вставки (возможно пакетами?)
        //List<Map<String, Object>> track = generateTrackByPoints(vehicleId, lon, lat, radius, speed, length);
        List<VehicleTrack> track = generateTrackByPoints(vehicleId, lon, lat, radius, speed, length);
        final int packSize = 10;
        int packs = track.size() / packSize;
        return Mono.defer(() -> {
            String authToken = tokenHolder.get();
            if (authToken == null) return Mono.error(new IllegalStateException("No token. Call getToken() first"));
            return Flux.range(0, packs+1)
                    .concatMap(i -> {
                        List<VehicleTrack> pack = track.subList(i * packSize, Math.max(i * packSize + packSize, track.size()));
                        return webClient.post()
                                .uri("api/tracks/" + vehicleId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                                .bodyValue(pack)
                                .retrieve()
                                .bodyToMono(Void.class)
                                .doOnSuccess(v -> {
                                    pointsCnt.incrementAndGet();
                                    System.out.println("Generation in progress. Next point inserted.");
                                });
                    }).then(Mono.fromCallable(pointsCnt::get));
        });
    }*/
}
