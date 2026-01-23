package org.skillsmart.veholder.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.dto.*;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.service.DashboardService;
import org.skillsmart.veholder.service.TripService;
import org.skillsmart.veholder.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping(value = "api/enterprises")
public class EnterpriseRestController {

    @Autowired
    private EnterpriseRepository repo;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private final ObjectMapper objectMapper;
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private TripService tripService;
    @Autowired
    private final KafkaTemplate<String, StatisticEvent> kafkaTemplateStatistic;

    public EnterpriseRestController(ObjectMapper objectMapper, KafkaTemplate<String, StatisticEvent> kafkaTemplateStatistic) {
        this.objectMapper = objectMapper;
        this.kafkaTemplateStatistic = kafkaTemplateStatistic;
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

    /*@GetMapping(value = "/{id}/vehicles")
    public Mono<ResponseEntity<Map<String, Object>>> getVehicleFromEnterpriseById(@PathVariable Long id, Pageable pageable) {
        return vehicleService.getPagingVehiclesByEnterprise(pageable, id)
                .map(page -> {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("currentPage", page.getNumber());
                    response.put("totalItems", page.getTotalElements());
                    response.put("totalPages", page.getTotalPages());
                    response.put("vehicles", page.getContent());
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    // Обработка ошибок
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }*/

    /*@GetMapping(value = "/{id}/vehicles")
    public Mono<ResponseEntity<Map<String, Object>>> getVehicleFromEnterpriseById(
            @PathVariable Long id,
            Pageable pageable) {

        return Mono.fromCallable(() -> vehicleService.getPagingVehiclesByEnterprise(pageable, id))
                .subscribeOn(Schedulers.boundedElastic()) // Выполняем в отдельном потоке
                .map(page -> {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("currentPage", page.getNumber());
                    response.put("totalItems", page.getTotalElements());
                    response.put("totalPages", page.getTotalPages());
                    response.put("vehicles", page.getContent());
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    log.error("Error getting vehicles for enterprise {}: {}", id, e.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }*/

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
            StatisticEvent statisticEvent = new StatisticEvent("enterprise", "update", LocalDateTime.now());
            kafkaTemplateStatistic.send("veholder-stats", statisticEvent);
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

    /*@GetMapping("/{enterpriseId}/dashboard")
    public Single<ResponseEntity<DashboardData>> getDashboard(@PathVariable Long enterpriseId) {
        return dashboardService.getEnterpriseDashboard(enterpriseId)
                .map(ResponseEntity::ok)
                .doOnSuccess(data -> log.info("Dashboard data loaded for enterprise {}", enterpriseId))
                .doOnError(error -> log.error("Failed to load dashboard for enterprise {}", enterpriseId, error))
                .onErrorReturn(error -> {
                    log.error("Error in dashboard controller for enterprise {}", enterpriseId, error);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(createErrorDashboardData());
                });
    }*/

    @GetMapping("/{enterpriseId}/dashboard")
    public ResponseEntity<DashboardData> getDashboard(@PathVariable Long enterpriseId) {
        try {
            DashboardData data = dashboardService.getEnterpriseDashboard(enterpriseId)
                    .blockingGet(); // Синхронное ожидание результата

            log.info("Dashboard data loaded for enterprise {}", enterpriseId);
            return ResponseEntity.ok(data);

        } catch (Exception error) {
            log.error("Failed to load dashboard for enterprise {}", enterpriseId, error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorDashboardData());
        }
    }

    private DashboardData createErrorDashboardData() {
        return new DashboardData("Error", 0, 0);
    }

    @GetMapping("/{enterpriseId}/trips")
    public ResponseEntity<List<TripDTO>> getTripsByEnterprise(@PathVariable Long enterpriseId) {
        List<TripDTO> response = tripService.getTripsByEnterpriseId(enterpriseId);
        return ResponseEntity.ok(response);
    }
}
