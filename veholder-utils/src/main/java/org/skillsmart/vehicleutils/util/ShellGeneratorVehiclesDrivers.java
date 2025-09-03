package org.skillsmart.vehicleutils.util;

import org.skillsmart.vehicleutils.rest.client.RestClient;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ShellComponent
public class ShellGeneratorVehiclesDrivers {

    private final RestClient restClient;
    private final Generator generator;

    public ShellGeneratorVehiclesDrivers(RestClient restClient, Generator generator) {
        this.restClient = restClient;
        this.generator = generator;
    }

    @ShellMethod(key = "hello", value = "Say hello")
    public String hello() {
        return "Hello, world!";
    }

    @ShellMethod(key = "generate", value = "Generate for enterprise {enterpriseId} vehicles {vehicleCount} with drivers")
    public void generate(
            @ShellOption(
                    value = {"-u", "--user"},
                    help = "Имя менеджера (обязательно)"
            ) String username,
            @ShellOption(
                    value = {"-p", "--pass"},
                    help = "Пароль (обязательно)"
            ) String pass,
            @ShellOption(
                    value = {"-e", "--enterprise"},
                    help = "Идентификатор(-ы) предприятия через запятую (обязательно)"
            ) String enterpriseIds,
            @ShellOption(
                    value = {"-v", "--vcnt"},
                    help = "Количество генерируемых автомобилей (обязательно)"
            ) int vehicleCount) {
        restClient.getToken(username, pass)
                .then(Mono.just("Auth completed")).block(Duration.ofSeconds(5));
        List<Map<String, Object>> brands = restClient.getBrands().block(Duration.ofSeconds(5));
        String[] enterpriseIdsArr = enterpriseIds.split(",");
        List<Long> enterpriseIdsList = new ArrayList<>();
        for (String s : enterpriseIdsArr) {
            enterpriseIdsList.add(Long.parseLong(s.trim()));
        }
        for (Long aLong : enterpriseIdsList) {
            restClient.generateVehiclesAndDrivers(aLong, vehicleCount, brands)
                    .subscribe(driverCount -> {
                        System.out.println("Generation complete. For Enterprise [" + aLong + "] Generated " + vehicleCount + " vehicles and " + driverCount + " active drivers");
                    });
        }
    }

    @ShellMethod(key = "track", value = "Generate for vehicle {vehicleId} track in real time (if time is null)")
    public void generateTrackForVehicle(
            @ShellOption(
                    value = {"-u", "--user"},
                    help = "Имя менеджера (обязательно)"
            ) String username,
            @ShellOption(
                    value = {"-p", "--pass"},
                    help = "Пароль (обязательно)"
            ) String pass,
            @ShellOption(
                    value = {"-v", "--vehicle"},
                    help = "Идентификатор автомобиля(обязательно)"
            ) Long vehicleId,
            @ShellOption(
                    value = {"-o", "--longitude"},
                    help = "долгота стартовой точки"
            ) String lon,
            @ShellOption(
                    value = {"-a", "--latitude"},
                    help = "широта стартовой точки"
            ) String lat,
            @ShellOption(
                    value = {"-r", "--flongitude"},
                    help = "долгота конечной точки"
            ) String flon,
            @ShellOption(
                    value = {"-l", "--flatitude"},
                    help = "широта конечной точки"
            ) String flat,
            @ShellOption(defaultValue = ShellOption.NULL,
                    value = {"-t", "--time"},
                    help = "время начала (yyyy-mm-ddThh24:mi:ss)"
            ) String startTime) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        restClient.getToken(username, pass)
                .then(Mono.just("Auth completed")).block(Duration.ofSeconds(5));

        restClient.generateVehicleTrack(vehicleId, lon, lat, flon, flat, startTime)
                .subscribe(packsCnt -> {
                    System.out.println("Generation complete. For vehicleId [" + vehicleId + "] Generated " + packsCnt + " geo points");
                });
    }

}
