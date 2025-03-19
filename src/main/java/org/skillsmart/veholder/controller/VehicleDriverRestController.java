package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.dto.VehicleDriverDto;
import org.skillsmart.veholder.service.VehicleDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/vehicle_drivers")
public class VehicleDriverRestController {

    @Autowired
    private VehicleDriverService service;

    @GetMapping(value = "")
    public ResponseEntity<List<VehicleDriverDto>> getVehicleDriverRelations() {
        return ResponseEntity.ok(service.getVehicleDriverRelations());
    }
}
