package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.dto.DriverNoDateDto;
import org.skillsmart.veholder.entity.dto.EnterpriseMileageResponse;
import org.skillsmart.veholder.entity.dto.MileageResponse;
import org.skillsmart.veholder.entity.dto.VehicleDTO;
import org.skillsmart.veholder.service.DriverService;
import org.skillsmart.veholder.service.MileageService;
import org.skillsmart.veholder.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/mileage")
public class MileageController {

    @Autowired
    private MileageService service;
    @Autowired
    private DriverService driverService;
    @Autowired
    private VehicleService vehicleService;

    @GetMapping(value = "/vehicle/{id}")
    public ResponseEntity<MileageResponse> getVehicleMileage(
            @PathVariable long id,
            @RequestParam String date) {
        Double mileage = service.getVehicleMileage(id, date);
        DriverNoDateDto driver = driverService.getDriverByVehicleId(id);
        Vehicle vehicle = vehicleService.getVehicleById(id);

        MileageResponse response = new MileageResponse();
        response.setVehicleId(id);
        response.setTotalMileage(mileage);
        response.setPeriod(date);
        response.setDriverName(driver.getName());
        response.setVehicleRegistrationNumber(vehicle.getRegistrationNumber());

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/enterprise/{id}")
    public ResponseEntity<EnterpriseMileageResponse> getEnterpriseMileage(
            @PathVariable long id,
            @RequestParam String date) {
        EnterpriseMileageResponse response = service.getEnterpriseMileage(id, date);
        return ResponseEntity.ok(response);
    }

}
