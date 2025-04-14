package org.skillsmart.veholder.entity.dto;

public class VehicleDriverDto {

    private Long id;
    private Long vehicleId;
    private Long driverId;
    private boolean active;

    public VehicleDriverDto(Long id, Long vehicleId, Long driverId, boolean active) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.active = active;
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

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "VehicleDriverDto{" +
                "id=" + id +
                ", vehicleId=" + vehicleId +
                ", driverId=" + driverId +
                ", active=" + active +
                '}';
    }
}
