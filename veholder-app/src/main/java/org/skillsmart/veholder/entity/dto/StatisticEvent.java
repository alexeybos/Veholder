package org.skillsmart.veholder.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticEvent {
    private String entityName;
    private String eventType;
    private LocalDateTime eventTime;
}
