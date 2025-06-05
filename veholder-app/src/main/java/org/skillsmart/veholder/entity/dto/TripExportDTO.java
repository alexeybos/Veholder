package org.skillsmart.veholder.entity.dto;

import com.vladmihalcea.hibernate.type.range.Range;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.skillsmart.veholder.entity.Trip;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TripExportDTO {
    private Long id;
    private Long vehicleId;
    private Range<ZonedDateTime> timeInterval;

    public TripExportDTO(Trip trip) {
        this.id = trip.getId();
        this.vehicleId = trip.getVehicle().getId();
        this.timeInterval = trip.getTimeInterval();
    }
}
