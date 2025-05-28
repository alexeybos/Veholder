package org.skillsmart.veholder.repository.spec;

import org.skillsmart.veholder.entity.VehicleTrack;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

public class TrackSpecifications {

    public static Specification<VehicleTrack> timeIntervalIn(ZonedDateTime start, ZonedDateTime end) {
        return (root, query, cb) -> cb.and(
                cb.between(root.get("recordedAt"), start, end)
        );
    }

    public static Specification<VehicleTrack> byVehicleId(Long vehicleId) {
        return (root, query, cb) -> cb.equal(root.get("vehicleId"), vehicleId);
    }

}
