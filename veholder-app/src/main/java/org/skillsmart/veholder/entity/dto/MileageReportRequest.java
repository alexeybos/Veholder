package org.skillsmart.veholder.entity.dto;

import org.skillsmart.veholder.entity.Period;

import java.time.LocalDateTime;

public record MileageReportRequest (
        Long vehicleId,
        Period period,
        LocalDateTime startDate,
        LocalDateTime endDate
) implements NewReportRequest {
    @Override
    public ReportRequest.ReportType type() {
        return ReportRequest.ReportType.MILEAGE;
    }
}
