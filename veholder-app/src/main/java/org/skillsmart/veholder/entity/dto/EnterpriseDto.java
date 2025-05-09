package org.skillsmart.veholder.entity.dto;

public record EnterpriseDto(
        Long id,
        String name,
        String city,
        String directorName,
        String timezone
) {}
