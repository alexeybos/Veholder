package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.dto.ReportRequest;
import org.skillsmart.veholder.entity.report.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportFactory {
    @Autowired
    private MileageReportService mileageReportService;

    @Autowired
    private DriverHoursReportService driverHoursReportService;

    /*@Autowired
    private DriverWorkTimeReportService driverWorkTimeReportService;*/

    public Report generateReport(ReportRequest request) throws Exception {
        switch (request.getReportType()) {
            case MILEAGE:
                return mileageReportService.generateMileageReport(
                        request.getVehicleId(),
                        request.getPeriod(),
                        request.getStartDate(),
                        request.getEndDate());
            case DRIVER_WORK_TIME:
                return driverHoursReportService.generateDriverHoursReport(
                        request.getVehicleId(),
                        request.getPeriod(),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getDriverName()
                );
            default:
                throw new IllegalArgumentException("Unknown report type");
        }
    }
}
