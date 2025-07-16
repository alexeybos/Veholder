package org.skillsmart.veholder.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.locationtech.jts.geom.Point;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Entity
@Table(name = "vehicle_tracks")
public class VehicleTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(nullable = false)
    //@Type(org.hibernate.spatial.JTSGeometryType)
    private Point point;

    @Column(name = "recorded_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]X", timezone = "UTC")
    private ZonedDateTime recordedAt;

    public VehicleTrack() {
    }

    public VehicleTrack(Long vehicleId, Point point, Instant recordedAt) {
        this.vehicleId = vehicleId;
        this.point = point;
        this.recordedAt = recordedAt.atZone(ZoneOffset.UTC);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public ZonedDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(ZonedDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
