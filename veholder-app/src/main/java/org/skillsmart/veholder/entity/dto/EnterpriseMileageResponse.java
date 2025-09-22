package org.skillsmart.veholder.entity.dto;

import java.util.List;

public class EnterpriseMileageResponse {
    private Long enterpriseId;
    private String enterpriseName;
    private List<MileageResponse> vehicleMileages;
    private Double totalMileage;
    private String period;

    public EnterpriseMileageResponse() {
    }

    public EnterpriseMileageResponse(Long enterpriseId, String enterpriseName, List<MileageResponse> vehicleMileages, Double totalMileage, String period) {
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.vehicleMileages = vehicleMileages;
        this.totalMileage = totalMileage;
        this.period = period;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public List<MileageResponse> getVehicleMileages() {
        return vehicleMileages;
    }

    public void setVehicleMileages(List<MileageResponse> vehicleMileages) {
        this.vehicleMileages = vehicleMileages;
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
}
