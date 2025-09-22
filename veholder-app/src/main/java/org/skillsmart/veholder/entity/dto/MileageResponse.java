package org.skillsmart.veholder.entity.dto;

public class MileageResponse {
    private Long vehicleId;
    private String vehicleRegistrationNumber;
    private Double totalMileage;
    private String period;
    private String driverName;

    public MileageResponse() {
    }

    public MileageResponse(Long vehicleId, String vehicleRegistrationNumber, Double totalMileage, String period, String driverName) {
        this.vehicleId = vehicleId;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
        this.totalMileage = totalMileage;
        this.period = period;
        this.driverName = driverName;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public Double getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(Double totalMileage) {
        this.totalMileage = totalMileage;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
