package org.skillsmart.veholder.entity.dto;

import com.vladmihalcea.hibernate.type.range.Range;

import java.time.ZonedDateTime;

public record TripDTO(
        Long id,
        Long vehicleId,
        Range<ZonedDateTime> timeInterval
) {}
