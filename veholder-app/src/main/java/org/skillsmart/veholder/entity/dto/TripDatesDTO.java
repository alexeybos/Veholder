package org.skillsmart.veholder.entity.dto;

import java.time.Instant;

public record TripDatesDTO(
        Long id,
        Long vehicleId,
        Instant startInterval,
        Instant endInterval
) {}
