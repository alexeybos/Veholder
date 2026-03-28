package org.skillsmart.veholder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
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
@Tag(name = "Бренды", description = "Работа с брендами")
public class BrandRestController {

    @Autowired
    BrandRepository repo;

    /*@Autowired
    JdbcBrandService jdbcBrandService;*/

    @Autowired
    BrandService brandService;

    @GetMapping
    @Operation(
            summary = "Список брендов",
            description = "Получить список всех доступных брендов"
    )
    public ResponseEntity<List<Brand>> getBrands() {
        Sort sortBy = Sort.by("id").ascending();
        return new ResponseEntity<>(repo.findAll(sortBy), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @Operation(
            summary = "Информация по бренду",
            description = "Получить информацию по конкретному бренду"
    )
    public ResponseEntity<Brand> getBrandById(@PathVariable @Parameter(description = "Идентификатор бренда", required = true, example = "101") Long id) {
        Brand brand = repo.getReferenceById(id);
        return new ResponseEntity<>(brand, HttpStatus.OK);
    }

    @Operation(
            summary = "Поиск бренда по имени",
            description = "Найти бренд по имени"
    )
    @GetMapping(value = "/find")
    public ResponseEntity<Brand> getBrandByName(@RequestParam @Parameter(description = "Имя бренда", example = "Lada") String name) {
        log.info("Getting brand with name = {}", name);
        Brand brand = repo.findByName(name);
        log.debug("Returning brand = {}", brand);
        return new ResponseEntity<>(brand, HttpStatus.OK);
    }

    /*@PostMapping
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
    }*/

    @PutMapping(value = "/{id}")
    @Operation(
            summary = "Редактировать бренд",
            description = "Смена характеристики бренда"
    )
    public ResponseEntity<?> updateBrand(@PathVariable @Parameter(description = "Идентификатор бренда", required = true, example = "101") @Min(0) Long id,
                                         @RequestBody @Parameter(description = "Набор характеристик бренда") BrandDto brandDto) {
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
