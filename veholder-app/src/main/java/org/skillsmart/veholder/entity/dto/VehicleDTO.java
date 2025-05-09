package org.skillsmart.veholder.entity.dto;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class VehicleDTO {

    private Long id;

    private int yearOfProduction;
    private double price;
    private String color;
    private int mileage;
    private String registrationNumber;
    private boolean inOrder;
    private Long brandId;
    private Long enterpriseId;
    private String brandName;
    private Instant purchaseDateTimeUTC;
    private Timestamp purchaseDateTime;

    public VehicleDTO() {
    }

    public VehicleDTO(Long id, int yearOfProduction, double price, String color, int mileage, String registrationNumber,
                      boolean inOrder, Long brandId, Long enterpriseId, String brandName, Instant purchaseDateTimeUTC, Timestamp purchaseDateTime) {
        this.id = id;
        this.yearOfProduction = yearOfProduction;
        this.price = price;
        this.color = color;
        this.mileage = mileage;
        this.registrationNumber = registrationNumber;
        this.inOrder = inOrder;
        this.brandId = brandId;
        this.enterpriseId = enterpriseId;
        this.brandName = brandName;
        this.purchaseDateTimeUTC = purchaseDateTimeUTC;
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Instant getPurchaseDateTimeUTC() {
        return purchaseDateTimeUTC;
    }

    public void setPurchaseDateTimeUTC(Instant purchaseDateTimeUTC) {
        this.purchaseDateTimeUTC = purchaseDateTimeUTC;
    }

    public String getLocalPurchaseDateTime(ZoneId targetZone) {
        return ZonedDateTime.ofInstant(purchaseDateTimeUTC, targetZone)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                //.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Timestamp getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public void setPurchaseDateTime(Timestamp purchaseDateTime) {
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
                ", brandName='" + brandName + '\'' +
                ", purchaseDateTime=" + purchaseDateTimeUTC +
                '}';
    }

}
