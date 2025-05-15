package org.skillsmart.veholder.entity.dto;

import org.skillsmart.veholder.entity.VehicleTrack;

import java.time.ZonedDateTime;

public class VehicleTrackDto {
    //private Long id;
    private Long vehicleId;
    private Double longitude;
    private Double latitude;
    private ZonedDateTime recordedAt;

    public VehicleTrackDto(VehicleTrack track) {
        //this.id = track.getId();
        this.vehicleId = track.getVehicleId();
        this.longitude = track.getPoint().getX();
        this.latitude = track.getPoint().getY();
        this.recordedAt = track.getRecordedAt();
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public ZonedDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(ZonedDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
