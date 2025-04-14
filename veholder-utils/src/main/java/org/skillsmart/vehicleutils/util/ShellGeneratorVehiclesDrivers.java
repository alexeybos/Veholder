package org.skillsmart.vehicleutils.util;

import org.skillsmart.vehicleutils.rest.client.RestClient;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Mono;

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

    /*@ShellMethod(key = "test", value = "Return list of Enterprises from REST")
    public String test(String username, String pass) {
        restClient.getToken("man1", "1111")
                .then(Mono.just("Auth completed")).block(Duration.ofSeconds(2));
        return restClient.getEnterprises().block(Duration.ofSeconds(2));
    }

    @ShellMethod(key = "brands", value = "Return list of Brands from DB")
    public List<Brand> brands() {
        return generator.getBrands();
    }*/

    //@ShellMethod

    //@ShellMethod(key = "generate", value = "Generate for enterprise {enterpriseId} vehicles {vehicleCount} and drivers {driverCount}")
    //public String generate(Long enterpriseId, int vehicleCount, int driverCount) {
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
        //int driverCount = generateVehiclesAndDrivers(enterpriseId, vehicleCount, brands);
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

        //return "Generation complete. Generated " + vehicleCount + " vehicles and " + driverCount + "drivers";
    }

    /*private int generateVehiclesAndDrivers(Long enterpriseId, int vehicleCnt, List<Map<String, Object>> brands) {
        //minValue + (int) (Math.random() * (maxValue - minValue + 1))
        int driverCnt = 0;
        for (int i = 0; i < vehicleCnt; i++) {
            VehicleDTO vehicle = new VehicleDTO();
            vehicle.setBrandId((Long) brands.get((int) (Math.random() * (brands.size()))).get("id"));
            vehicle.setRegistrationNumber(generateNumber());
            vehicle.setPrice(20000 + (Math.random() * (25000000 - 20000)));
            vehicle.setMileage(100 + (int) (Math.random() * (200000 - 100)));
            vehicle.setInOrder(true);
            vehicle.setColor(getColor());
            vehicle.setEnterpriseId(enterpriseId);
            vehicle.setYearOfProduction(1955 + (int) (Math.random() * (2025 - 1955 + 1)));
            //System.out.println(vehicle);
            if (i % 3 == 0) {
                DriverDto driverDto = new DriverDto();
                driverDto.setVehicleId(1L);
                driverDto.setBirthDate(generateDate());
                driverDto.setActive(true);
                driverDto.setName(getName());
                driverDto.setSalary(20000 + (Math.random() * (250000 - 20000)));
                driverDto.setEnterpriseId(enterpriseId);
                driverCnt++;
                System.out.println(driverDto);
            }
        }
        return driverCnt;
    }*/

}
