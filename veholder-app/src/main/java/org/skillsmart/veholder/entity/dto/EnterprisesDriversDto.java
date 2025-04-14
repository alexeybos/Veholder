package org.skillsmart.veholder.entity.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record EnterprisesDriversDto(
        Long id,
        String name,
        String driversInfo
) {}
