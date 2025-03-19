package org.skillsmart.veholder.controller;

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
@RequestMapping("api/enterprises")
public class EnterpriseRestController {

    @Autowired
    private EnterpriseService service;

    @GetMapping(value = "")
    public ResponseEntity<List<Enterprise>> getEnterprises() {
        return new ResponseEntity<>(service.getEnterprises(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Enterprise> getEnterpriseById(@PathVariable Long id) {
        Enterprise enterprise = service.getEnterpriseById(id);
        if (enterprise != null) return ResponseEntity.ok(enterprise);
        return ResponseEntity.notFound().build();
    }
}
