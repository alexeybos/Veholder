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
    private ZonedDateTime tripStart;
    private ZonedDateTime tripEnd;
//    private String startPointDesc;
//    private String endPointDesc;
    private Double startLon;
    private Double startLat;
    private Double endLon;
    private Double endLat;

    public TripExportDTO(Trip trip, Double startLon, Double startLat, Double endLon, Double endLat) {
        this.id = trip.getId();
        this.vehicleId = trip.getVehicle().getId();
        this.tripStart = trip.getTimeInterval().lower();
        this.tripEnd = trip.getTimeInterval().upper();
        this.startLon = startLon;
        this.startLat = startLat;
        this.endLon = endLon;
        this.endLat = endLat;
    }
}
