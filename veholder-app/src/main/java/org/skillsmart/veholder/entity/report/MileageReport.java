package org.skillsmart.veholder.entity.report;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import org.skillsmart.veholder.entity.Vehicle;

@Entity
@DiscriminatorValue("MILEAGE")
public class MileageReport extends Report {
    @ManyToOne
    private Vehicle vehicle;

    private Double totalMileage;

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setTotalMileage(double total) {
        this.totalMileage = total;
    }

    @Transient
    public static final String reportTypeName = "Отчет по пробегу";
    @Transient
    public static final String reportTypeId = "MILEAGE";

    public String getReportTypeId() {
        return reportTypeId;
    }

    public String getReportTypeName() {
        return reportTypeName;
    }
}
