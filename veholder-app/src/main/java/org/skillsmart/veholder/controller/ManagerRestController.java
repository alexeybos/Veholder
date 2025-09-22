package org.skillsmart.veholder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.skillsmart.veholder.entity.dto.ManagerDTO;
import org.skillsmart.veholder.service.ManagerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "")
public class ManagerRestController {

    @Autowired
    private ManagerDetailsService service;

    /*@GetMapping(value = "")
    public ResponseEntity<List<Enterprise>> getEnterprises() {
        return new ResponseEntity<>(service.getEnterprises(), HttpStatus.OK);
    }*/

    @GetMapping(value = "api/admin/managers")
    public ResponseEntity<List<ManagerDTO>> getManagers() {
        return new ResponseEntity<>(service.getManagersList(), HttpStatus.OK);
    }

    @PostMapping(value = "post/managers")
    public ResponseEntity<List<ManagerDTO>> getEnterpriseById() {
        return new ResponseEntity<>(service.getManagersList(), HttpStatus.OK);
    }

    /*@GetMapping(value = "/{id}")
    public ResponseEntity<JsonNode> getEnterpriseById(@PathVariable Long id) {
        JsonNode enterprise = service.getFullEnterpriseInfoById(id);
        if (enterprise != null) return ResponseEntity.ok(enterprise);
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/drivers")
    public ResponseEntity<JsonNode> getDriversFromEnterpriseById(@PathVariable Long id) {
        //Enterprise enterprise = service.getEnterpriseById(id);
        JsonNode enterprises = service.getDriversByEnterpriseIdJson(id);
        if (enterprises != null) return ResponseEntity.ok(enterprises);
        return ResponseEntity.notFound().build();
    }*/
}
