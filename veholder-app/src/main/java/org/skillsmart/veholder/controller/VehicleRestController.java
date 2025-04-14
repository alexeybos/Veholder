package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleProjection;
import org.skillsmart.veholder.entity.dto.VehicleDTO;
import org.skillsmart.veholder.service.VehicleService;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    /*@GetMapping(value = "")
    public ResponseEntity<List<VehicleProjection>> getVehiclesLazy() {
        Sort sortBy = Sort.by("id").ascending();
        return new ResponseEntity<>(service.getOnlyVehiclesList(sortBy), HttpStatus.OK);
    }*/

    @GetMapping(value = "")
    public ResponseEntity<List<VehicleProjection>> getVehiclesLazy() {
        Sort sortBy = Sort.by("id").ascending();
        return new ResponseEntity<>(service.getOnlyVehiclesListForManager(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<VehicleProjection> getVehicle(@PathVariable Long id) {
        return new ResponseEntity<>(service.getVehicleProjectedById(id), HttpStatus.OK);
    }

    /*@PostMapping(value = "")
    public ResponseEntity<Long> createVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(service.createVehicle(vehicle));
    }*/

    @PostMapping(value = "")
    public ResponseEntity<?> createVehicle(@RequestBody VehicleDTO vehicle) {
        Vehicle vehicleFullObject = new Vehicle();
        vehicleFullObject.setBrand(new Brand(vehicle.getBrandId()));
        vehicleFullObject.setColor(vehicle.getColor());
        vehicleFullObject.setEnterprise(new Enterprise(vehicle.getEnterpriseId()));
        vehicleFullObject.setMileage(vehicle.getMileage());
        vehicleFullObject.setInOrder(vehicle.isInOrder());
        vehicleFullObject.setPrice(vehicle.getPrice());
        vehicleFullObject.setRegistrationNumber(vehicle.getRegistrationNumber());
        vehicleFullObject.setYearOfProduction(vehicle.getYearOfProduction());
        try {
            Long result = service.createVehicle(vehicleFullObject);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @RequestBody Map<String, Object> vehicle) {
        if (!Objects.equals(id, vehicle.getOrDefault("id", id))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Bad Request",
                    "message", "Wrong id for update",
                    "timestamp", LocalDateTime.now()
            ));
        }
        try {
            service.updateVehicle(id, vehicle);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        try {
            service.deleteVehicle(id);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }

    }
    //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //        String username = authentication.getName();
}
