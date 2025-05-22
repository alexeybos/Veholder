package org.skillsmart.veholder.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.range.PostgreSQLRangeType;
import com.vladmihalcea.hibernate.type.range.Range;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "time_interval", columnDefinition = "tstzrange")
    @Type(PostgreSQLRangeType.class)
    private Range<ZonedDateTime> timeInterval;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Range<ZonedDateTime> getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Range<ZonedDateTime> timeInterval) {
        this.timeInterval = timeInterval;
    }
}
