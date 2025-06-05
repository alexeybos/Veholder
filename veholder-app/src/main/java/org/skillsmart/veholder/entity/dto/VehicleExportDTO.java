package org.skillsmart.veholder.entity.dto;

import org.skillsmart.veholder.entity.Vehicle;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class VehicleExportDTO {

    private Long id;

    private int yearOfProduction;
    private double price;
    private String color;
    private int mileage;
    private String registrationNumber;
    private boolean inOrder;
    private Long brandId;
    private Long enterpriseId;
    private Instant purchaseDateTime;

    public VehicleExportDTO() {
    }

    public VehicleExportDTO(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.yearOfProduction = vehicle.getYearOfProduction();
        this.price = vehicle.getPrice();
        this.color = vehicle.getColor();
        this.mileage = vehicle.getMileage();
        this.registrationNumber = vehicle.getRegistrationNumber();
        this.inOrder = vehicle.isInOrder();
        this.brandId = vehicle.getBrand().getId();
        this.enterpriseId = vehicle.getEnterprise().getId();
        this.purchaseDateTime = vehicle.getPurchaseDateTime();
    }

    public VehicleExportDTO(Long id, int yearOfProduction, double price, String color, int mileage, String registrationNumber,
                            boolean inOrder, Long brandId, Long enterpriseId, Instant purchaseDateTime) {
        this.id = id;
        this.yearOfProduction = yearOfProduction;
        this.price = price;
        this.color = color;
        this.mileage = mileage;
        this.registrationNumber = registrationNumber;
        this.inOrder = inOrder;
        this.brandId = brandId;
        this.enterpriseId = enterpriseId;
        this.purchaseDateTime = purchaseDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(int yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public boolean isInOrder() {
        return inOrder;
    }

    public void setInOrder(boolean inOrder) {
        this.inOrder = inOrder;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Instant getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public void setPurchaseDateTime(Instant purchaseDateTime) {
        this.purchaseDateTime = purchaseDateTime;
    }

    @Override
    public String toString() {
        return "VehicleDTO{" +
                "id=" + id +
                ", yearOfProduction=" + yearOfProduction +
                ", price=" + price +
                ", color='" + color + '\'' +
                ", mileage=" + mileage +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", inOrder=" + inOrder +
                ", brandId=" + brandId +
                ", enterpriseId=" + enterpriseId +
                '}';
    }

}
