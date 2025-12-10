package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.dto.BrandDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;

@Service
public class JdbcBrandService {
    private final JdbcTemplate jdbcTemplate;

    public JdbcBrandService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void createBrandWithJdbc(BrandDto brandDto) {
        jdbcTemplate.update(
                "INSERT INTO brand (name, type, load_capacity, tank, number_of_seats) VALUES (?, ?, ?, ?, ?)",
                brandDto.name(), brandDto.type(), brandDto.loadCapacity(), brandDto.tank(), brandDto.numberOfSeats()
        );
        if (brandDto.name() == null) {
            throw new InvalidParameterException("Name cannot be null");
        }
    }
}
