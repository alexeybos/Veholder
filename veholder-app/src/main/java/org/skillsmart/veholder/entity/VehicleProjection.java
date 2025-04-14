package org.skillsmart.veholder.entity;

public interface VehicleProjection {
    Long getId();
    Long getBrandId();
    int getYearOfProduction();
    double getPrice();
    String getColor();
    int getMileage();
    String getRegistrationNumber();
    boolean isInOrder();
    Long getEnterpriseId();
}
