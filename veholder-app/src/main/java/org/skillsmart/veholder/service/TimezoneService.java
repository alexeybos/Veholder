package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.VehicleProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TimezoneService {

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private EnterpriseService enterpriseService;

    public ZoneId getEnterpriseTimeZoneByVehicle(Long vehicleId) {
        VehicleProjection vehicle = vehicleService.getVehicleProjectedById(vehicleId);
        Enterprise enterprise = enterpriseService.getEnterpriseById(vehicle.getEnterpriseId());
        return enterprise.getTimezone();
    }

    public String getFormattedDateTimeInEnterpriseZone(Instant dateTime, ZoneId targetZone) {
        return ZonedDateTime.ofInstant(dateTime, targetZone)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    public String getFormattedDateTimeInEnterpriseZone(ZonedDateTime dateTime, ZoneId targetZone) {
        return dateTime.withZoneSameInstant(targetZone).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    public ZonedDateTime getDateTimeInEnterpriseZone(ZonedDateTime dateTime, ZoneId targetZone) {
        return dateTime.withZoneSameInstant(targetZone);
    }
}
