package org.skillsmart.veholder.entity.dto;

import org.skillsmart.veholder.entity.VehicleTrack;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class VehicleTrackDto {
    private Long id;
    private Long vehicleId;
    private Double lon;
    private Double lat;
    private ZonedDateTime recordedAt;
    private String recordedEnterpriseZone;

    public VehicleTrackDto(VehicleTrack track) {
        this.id = track.getId();
        this.vehicleId = track.getVehicleId();
        this.lon = track.getPoint().getX();
        this.lat = track.getPoint().getY();
        this.recordedAt = track.getRecordedAt();
        this.recordedEnterpriseZone = "";
    }

    public VehicleTrackDto(VehicleTrack track, ZoneId targetZone) {
        this.id = track.getId();
        this.vehicleId = track.getVehicleId();
        this.lon = track.getPoint().getX();
        this.lat = track.getPoint().getY();
        this.recordedAt = track.getRecordedAt();
        this.recordedEnterpriseZone = track.getRecordedAt().withZoneSameInstant(targetZone).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
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

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public ZonedDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(ZonedDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getRecordedEnterpriseZone() {
        return recordedEnterpriseZone;
    }

    public void setRecordedEnterpriseZone(String recordedEnterpriseZone) {
        this.recordedEnterpriseZone = recordedEnterpriseZone;
    }
}
