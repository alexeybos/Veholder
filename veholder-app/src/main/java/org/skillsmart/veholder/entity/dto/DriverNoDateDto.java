package org.skillsmart.veholder.entity.dto;

import java.time.LocalDate;

public class DriverNoDateDto {
    private Long id;
    private String name;
    private double salary;
    private Long enterpriseId;
    private Long vehicleId;
    private boolean isActive;

    public DriverNoDateDto(Long id, String name, double salary, Long enterpriseId,
                           Long vehicleId, boolean isActive) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.enterpriseId = enterpriseId;
        this.vehicleId = vehicleId;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
