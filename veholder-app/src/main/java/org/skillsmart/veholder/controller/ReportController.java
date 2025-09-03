package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.report.Report;
import org.skillsmart.veholder.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    /*@GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }*/

    @GetMapping
    public List<Map<String, Object>> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public Report getReport(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    @PostMapping
    public Report getReport(@RequestBody Map<String, Object> parameters) throws Exception {
        return reportService.getReportByParams(parameters);
    }
}
