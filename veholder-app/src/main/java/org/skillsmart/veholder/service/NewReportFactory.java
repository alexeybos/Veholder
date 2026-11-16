package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.dto.DriverHoursReportRequest;
import org.skillsmart.veholder.entity.dto.MileageReportRequest;
import org.skillsmart.veholder.entity.dto.NewReportRequest;
import org.skillsmart.veholder.entity.report.Report;
import org.springframework.beans.factory.annotation.Autowired;

public class NewReportFactory {
    @Autowired
    private MileageReportService mileageReportService;

    @Autowired
    private DriverHoursReportService driverHoursReportService;


    public Report generateReport(NewReportRequest request) throws Exception {
        return switch (request) {
            case MileageReportRequest req -> mileageReportService.generateMileageReport(
                    req.vehicleId(),
                    req.period(),
                    req.startDate(),
                    req.endDate()
            );
            case DriverHoursReportRequest req -> driverHoursReportService.generateDriverHoursReport(
                    req.vehicleId(),
                    req.period(),
                    req.startDate(),
                    req.endDate(),
                    req.driverName()
            );
        };
    }
}
