package org.skillsmart.veholder.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.skillsmart.veholder.entity.Period;
import org.skillsmart.veholder.entity.report.MileageReport;
import org.skillsmart.veholder.service.MileageReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports/mileage")
public class MileageReportController {
    @Autowired
    private MileageReportService mileageReportService;

    @PostMapping
    public MileageReport generateMileageReport(
            @RequestParam Long vehicleId,
            @RequestParam Period period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) throws JsonProcessingException {

        return mileageReportService.generateMileageReport(vehicleId, period, startDate, endDate);
    }

}
