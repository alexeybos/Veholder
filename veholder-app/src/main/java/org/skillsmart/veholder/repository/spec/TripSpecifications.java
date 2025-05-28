package org.skillsmart.veholder.repository.spec;

import com.vladmihalcea.hibernate.type.range.Range;
import org.skillsmart.veholder.entity.Trip;
import org.springframework.data.jpa.domain.Specification;
import java.time.ZonedDateTime;

public class TripSpecifications {

    public static Specification<Trip> timeIntervalOverlaps(ZonedDateTime start, ZonedDateTime end) {
        return (root, query, cb) -> cb.and(
                cb.lessThan(
                        cb.function("lower",
                                ZonedDateTime.class,
                                cb.function("pg_catalog.tstzrange",
                                        String.class,
                                        root.get("timeInterval"))
                        ),
                        end
                ),
                cb.greaterThan(
                        cb.function("upper",
                                ZonedDateTime.class,
                                cb.function("pg_catalog.tstzrange",
                                        String.class,
                                        root.get("timeInterval"))
                        ),
                        start
                )
        );
    }

    public static Specification<Trip> byVehicleId(Long vehicleId) {
        return (root, query, cb) -> cb.equal(root.get("vehicle").get("id"), vehicleId);
    }
}
