package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/brands")
public class BrandRestController {

    @Autowired
    BrandRepository repo;

    @GetMapping(value = "")
    public ResponseEntity<List<Brand>> getBrands() {
        Sort sortBy = Sort.by("id").ascending();
        return new ResponseEntity<>(repo.findAll(sortBy), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        Brand brand = repo.getReferenceById(id);
        return new ResponseEntity<>(brand, HttpStatus.OK);
    }
}
