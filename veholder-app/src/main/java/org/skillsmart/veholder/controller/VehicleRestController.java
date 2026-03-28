package org.skillsmart.veholder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleProjection;
import org.skillsmart.veholder.entity.dto.VehicleDTO;
import org.skillsmart.veholder.service.VehicleService;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("api/vehicles")
@Tag(name = "Автомобили", description = "Работа с автомобилями")
public class VehicleRestController {

    @Autowired
    private VehicleService service;

    @GetMapping(value = "")
    @Operation(
            summary = "Получить список всех автомобилей",
            description = "Получить список всех автомобилей (с разбивкой по страницам)"
    )
    public ResponseEntity<Map<String, Object>> getVehiclesPages(Pageable pageable) {
        Page<VehicleProjection> page = service.getPagingVehicles(pageable);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("vehicles", page.getContent());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}")
    @Operation(
            summary = "Информация по автомобилю",
            description = "Получить описание конкретного автомобиля"
    )
    public ResponseEntity<VehicleProjection> getVehicle(@PathVariable Long id) {
        return new ResponseEntity<>(service.getVehicleProjectedById(id), HttpStatus.OK);
    }

    @GetMapping(value = "vehiclesFull")
    @Operation(
            summary = "Расширенная информация по автомобилю",
            description = "Получить полную информацию по автомобилю"
    )
    public ResponseEntity<List<Vehicle>> getVehicles() {
        Sort sortBy = Sort.by("id").ascending();
        return new ResponseEntity<>(service.getList(sortBy), HttpStatus.OK);
    }

    @PostMapping(value = "")
    @Operation(
            summary = "Создать автомобиль",
            description = "создание нового автомобиля"
    )
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

    @Operation(
            summary = "Редактировать автомобиль",
            description = "Редактирование характеристик автомобиля"
    )
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

    @Operation(
            summary = "Удалить автомобиль",
            description = "Удаление указанного автомобиля"
    )
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
