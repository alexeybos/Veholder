package org.skillsmart.veholder.entity.dto;

public sealed interface NewReportRequest permits MileageReportRequest, DriverHoursReportRequest {
    ReportRequest.ReportType type();
}
