package org.skillsmart.veholder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.dto.EnterpriseDto;
import org.skillsmart.veholder.entity.dto.VehicleDTO;
import org.skillsmart.veholder.service.EnterpriseService;
import org.skillsmart.veholder.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/enterprises")
public class EnterpriseRestController {

    @Autowired
    private EnterpriseService service;
    @Autowired
    private VehicleService vehicleService;

    /*@GetMapping(value = "")
    public ResponseEntity<List<Enterprise>> getEnterprises() {
        return new ResponseEntity<>(service.getEnterprises(), HttpStatus.OK);
    }*/

    @GetMapping(value = "")
    public ResponseEntity<List<EnterpriseDto>> getEnterprises() {
        return new ResponseEntity<>(service.getEnterprisesByManager(), HttpStatus.OK);
    }

    /*@GetMapping(value = "/{id}")
    public ResponseEntity<Enterprise> getEnterpriseById(@PathVariable Long id) {
        Enterprise enterprise = service.getEnterpriseById(id);
        if (enterprise != null) return ResponseEntity.ok(enterprise);
        return ResponseEntity.notFound().build();
    }*/

    @GetMapping(value = "/{id}")
    public ResponseEntity<JsonNode> getEnterpriseById(@PathVariable Long id) {
        JsonNode enterprise = service.getFullEnterpriseInfoById(id);
        if (enterprise != null) return ResponseEntity.ok(enterprise);
        return ResponseEntity.notFound().build();
    }

    /*@GetMapping(value = "/{id}/drivers")
    public ResponseEntity<EnterprisesDriversDto> getDriversFromEnterpriseById(@PathVariable Long id) {
        //Enterprise enterprise = service.getEnterpriseById(id);
        EnterprisesDriversDto enterprises = service.getDriversByEnterpriseId(id);
        if (enterprises != null && !enterprises.isEmpty()) return ResponseEntity.ok(enterprises.getFirst());
        return ResponseEntity.notFound().build();
    }*/
    @GetMapping(value = "/{id}/drivers")
    public ResponseEntity<JsonNode> getDriversFromEnterpriseById(@PathVariable Long id) {
        //Enterprise enterprise = service.getEnterpriseById(id);
        JsonNode enterprises = service.getDriversByEnterpriseIdJson(id);
        if (enterprises != null) return ResponseEntity.ok(enterprises);
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/vehicles")
    public ResponseEntity<Map<String, Object>> getVehicleFromEnterpriseById(@PathVariable Long id, Pageable pageable) {
        Page<VehicleDTO> page = vehicleService.getPagingVehiclesByEnterprise(pageable, id);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("vehicles", page.getContent());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createEnterprise(@RequestBody Enterprise enterprise) {
        try {
            Long id = service.createEnterprise(enterprise);
            return ResponseEntity.ok(id);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateEnterprise(@PathVariable Long id, @RequestBody Map<String, Object> enterprise) {
        try {
            service.updateEnterprise(id, enterprise);
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
    public ResponseEntity<?> deleteEnterprise(@PathVariable Long id) {
        try {
            service.deleteEnterprise(id);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

}
