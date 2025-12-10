package org.skillsmart.veholder.controller;

import lombok.extern.slf4j.Slf4j;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.dto.BrandDto;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.service.BrandService;
import org.skillsmart.veholder.service.JdbcBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/brands")
@Slf4j
public class BrandRestController {

    @Autowired
    BrandRepository repo;

    @Autowired
    JdbcBrandService jdbcBrandService;

    @Autowired
    BrandService brandService;

    @GetMapping
    public ResponseEntity<List<Brand>> getBrands() {
        Sort sortBy = Sort.by("id").ascending();
        return new ResponseEntity<>(repo.findAll(sortBy), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        Brand brand = repo.getReferenceById(id);
        return new ResponseEntity<>(brand, HttpStatus.OK);
    }

    @GetMapping(value = "/find")
    public ResponseEntity<Brand> getBrandByName(@RequestParam String name) {
        log.info("Getting brand with name = {}", name);
        Brand brand = repo.findByName(name);
        log.debug("Returning brand = {}", brand);
        return new ResponseEntity<>(brand, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createBrand(@RequestBody BrandDto brandDto) {
        try {
            jdbcBrandService.createBrandWithJdbc(brandDto);
            return ResponseEntity.ok("OK");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (InvalidParameterException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Invalid Parameter",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Long id, @RequestBody BrandDto brandDto) {
        try {
            brandService.updateBrand(id, brandDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Internal error",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }

    }
}
