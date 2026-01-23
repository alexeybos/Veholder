package org.skillsmart.veholder.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleEvent {
    private EventType eventType;
    private Long vehicleId;
    private Long enterpriseId;
    //private Long managerId;
    private String username;
    private String vehicleName;
    private LocalDateTime eventTime;
    private Map<String, Object> changes; // Для обновлений

    public enum EventType {
        VEHICLE_CREATED,
        VEHICLE_UPDATED
    }
}
