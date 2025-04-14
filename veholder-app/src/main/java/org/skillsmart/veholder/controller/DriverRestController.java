package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.Driver;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.dto.DriverDto;
import org.skillsmart.veholder.entity.dto.DriverNoDateDto;
import org.skillsmart.veholder.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/drivers")
public class DriverRestController {

    @Autowired
    private DriverService service;

    /*@GetMapping(value = "")
    public ResponseEntity<List<DriverDto>> getDrivers() {
        return ResponseEntity.ok(service.getDriversDto());
    }*/

    @GetMapping(value = "")
    public ResponseEntity<List<DriverNoDateDto>> getDrivers() {
        return ResponseEntity.ok(service.getDriversDtoByManager());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Long id) {
        DriverDto driver = service.getDriverDTOById(id);
        if (driver != null) return ResponseEntity.ok(driver);
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createDriver(@RequestBody DriverDto driver) {
        Driver fullDriver = new Driver();
        fullDriver.setActive(driver.isActive());
        fullDriver.setEnterprise(new Enterprise(driver.getEnterpriseId()));
        fullDriver.setBirthDate(driver.getBirthDate());
        fullDriver.setName(driver.getName());
        fullDriver.setSalary(driver.getSalary());
        Vehicle vehicle = new Vehicle();
        vehicle.setId(driver.getVehicleId());
        fullDriver.setVehicle(vehicle);
        try {
            Long result = service.createDriver(fullDriver);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

}
