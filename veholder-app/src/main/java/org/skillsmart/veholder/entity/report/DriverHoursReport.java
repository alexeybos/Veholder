package org.skillsmart.veholder.entity.report;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import org.skillsmart.veholder.entity.Vehicle;

@Entity
@DiscriminatorValue("DRIVER_HOURS")
public class DriverHoursReport extends Report{
    @ManyToOne
    private Vehicle vehicle;

    private Double totalHours;

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }
}
