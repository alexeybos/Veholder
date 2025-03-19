package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.dto.DriverDto;
import org.skillsmart.veholder.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/drivers")
public class DriverRestController {

    @Autowired
    private DriverService service;

    @GetMapping(value = "")
    public ResponseEntity<List<DriverDto>> getDrivers() {
        return ResponseEntity.ok(service.getDriversDto());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Long id) {
        DriverDto driver = service.getDriverDTOById(id);
        if (driver != null) return ResponseEntity.ok(driver);
        return ResponseEntity.notFound().build();
    }
}
