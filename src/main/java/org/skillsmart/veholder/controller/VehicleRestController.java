package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleProjection;
import org.skillsmart.veholder.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/vehicles")
public class VehicleRestController {

    @Autowired
    private VehicleService service;

    @GetMapping(value = "vehiclesFull")
    public ResponseEntity<List<Vehicle>> getVehicles() {
        Sort sortBy = Sort.by("id").ascending();
        return new ResponseEntity<>(service.getList(sortBy), HttpStatus.OK);
    }

    @GetMapping(value = "")
    public ResponseEntity<List<VehicleProjection>> getVehiclesLazy() {
        Sort sortBy = Sort.by("id").ascending();
        return new ResponseEntity<>(service.getOnlyVehiclesList(sortBy), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<VehicleProjection> getVehicle(@PathVariable Long id) {
        return new ResponseEntity<>(service.getVehicleProjectedById(id), HttpStatus.OK);
    }
}
