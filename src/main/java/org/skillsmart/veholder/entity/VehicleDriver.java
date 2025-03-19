package org.skillsmart.veholder.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.AssertTrue;
import org.skillsmart.veholder.service.VehicleDriverService;


@Entity
@Table(name = "vehicle_driver", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"vehicle_id", "driver_id"})
})
public class VehicleDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    private boolean active;

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

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /*@AssertTrue(message = "Водитель может быть активным только на одной машине!")
    public boolean isDriverActiveOnOneVehicle() {
        if (!active) return true;
        VehicleDriverService service = ApplicationContextHolder.getApplicationContext().getBean
    }*/

    @Override
    public String toString() {
        return "VehicleDriver{" +
                "id=" + id +
                ", vehicle=" + vehicle +
                ", driver=" + driver +
                ", active=" + active +
                '}';
    }
}
