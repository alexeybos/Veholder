package org.skillsmart.veholder.entity.dto;

public record BrandDto(
        Long id,
        String name,
        String type,
        int loadCapacity,
        int tank,
        int numberOfSeats
) {}
