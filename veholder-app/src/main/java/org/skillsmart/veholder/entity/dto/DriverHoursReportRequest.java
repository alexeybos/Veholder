package org.skillsmart.veholder.entity.dto;

import org.skillsmart.veholder.entity.Period;

import java.time.LocalDateTime;

public record DriverHoursReportRequest(
        Long vehicleId,
        Period period,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String driverName
) implements NewReportRequest{

    @Override
    public ReportRequest.ReportType type() {
        return ReportRequest.ReportType.DRIVER_WORK_TIME;
    }
}
