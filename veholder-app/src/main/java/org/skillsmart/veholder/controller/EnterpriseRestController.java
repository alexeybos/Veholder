package org.skillsmart.veholder.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.dto.EnterpriseDto;
import org.skillsmart.veholder.entity.dto.VehicleDTO;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.service.EnterpriseService;
import org.skillsmart.veholder.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private EnterpriseRepository repo;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private final ObjectMapper objectMapper;

    public EnterpriseRestController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "")
    public ResponseEntity<List<EnterpriseDto>> getEnterprises() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(repo.getEnterprisesByManager(authentication.getName()), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<JsonNode> getEnterpriseById(@PathVariable Long id) {
        JsonNode enterprise;
        try {
            String result = repo.getFullEnterpriseInfoById(id);
            enterprise = objectMapper.readTree(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
        if (enterprise != null) return ResponseEntity.ok(enterprise);
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/export")
    public ResponseEntity<JsonNode> exportEnterpriseById(@PathVariable Long id,
                                                         @RequestParam(defaultValue = "json") String format) {
        JsonNode enterprise;
        try {
            String result = repo.getFullEnterpriseInfoById(id);
            enterprise = objectMapper.readTree(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
        if (enterprise != null) return ResponseEntity.ok(enterprise);
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/drivers")
    public ResponseEntity<JsonNode> getDriversFromEnterpriseById(@PathVariable Long id) {
        String result = repo.getDriversByEnterpriseJson(id);
        JsonNode enterprises;
        try {
            enterprises = objectMapper.readTree(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
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
        if (checkEnterpriseByManager(enterprise.getId())) {
            Enterprise created = repo.save(enterprise);
            return ResponseEntity.ok(created.getId());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", "Access Denied",
                "message", "Создание предприятия разрешено только менеджеру. Пользователь не является менеджером!",
                "timestamp", LocalDateTime.now()
        ));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateEnterprise(@PathVariable Long id, @RequestBody Map<String, Object> values) {
        try {
            if (!checkEnterpriseByManager(id)) {
                throw new AccessDeniedException("Можно редактировать только свое предприятие!");
            }
            Enterprise enterprise = repo.getReferenceById(id);
            enterprise.setCity(values.getOrDefault("city", enterprise.getCity()).toString());
            enterprise.setName(values.getOrDefault("name", enterprise.getName()).toString());
            enterprise.setDirectorName(values.getOrDefault("directorName", enterprise.getDirectorName()).toString());
            repo.save(enterprise);
            repo.flush();
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
            if (!checkEnterpriseByManager(id)) {
                throw new AccessDeniedException("Можно удалить только свое предприятие!");
            }
            repo.deleteEnterpriseManagersLink(id);
            repo.deleteEnterprise(id);
            repo.flush();
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    private boolean checkEnterpriseByManager(Long id) {
        return repo.checkEnterpriseByManager(id, getManagerName()) > 0;
    }

    private String getManagerName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
