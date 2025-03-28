package org.skillsmart.veholder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/enterprises")
public class EnterpriseRestController {

    @Autowired
    private EnterpriseService service;

    @GetMapping(value = "")
    public ResponseEntity<List<Enterprise>> getEnterprises() {
        return new ResponseEntity<>(service.getEnterprises(), HttpStatus.OK);
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
}
