package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.Period;
import org.skillsmart.veholder.entity.report.DriverHoursReport;
import org.skillsmart.veholder.service.DriverHoursReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports/driver")
public class DriverHoursReportController {

    @Autowired
    private DriverHoursReportService service;

    @PostMapping
    public DriverHoursReport generateMileageReport(
            @RequestParam Long vehicleId,
            @RequestParam Period period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam String driverName) throws Exception {

        return service.generateDriverHoursReport(vehicleId, period, startDate, endDate, driverName);
        //mileageReportService.generateMileageReport(vehicleId, period, startDate, endDate);
    }
}
