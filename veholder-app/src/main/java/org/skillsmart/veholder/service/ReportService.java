package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.report.DriverHoursReport;
import org.skillsmart.veholder.entity.report.MileageReport;
import org.skillsmart.veholder.entity.report.Report;
import org.skillsmart.veholder.repository.ReportRepository;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    /*public List<Report> getAllReports() {
        return reportRepository.findAll();
    }*/

    public List<Map<String, Object>> getAllReports() {
        List<Map<String, Object>> reports = new ArrayList<>();
        Map<String, Object> report = new HashMap<>();
        report.put("id", MileageReport.reportTypeId);
        report.put("name", MileageReport.reportTypeName);
        /*Map<String, String> params1 = new HashMap<>();
        params1.put("enterpriseId", "Предприятие");
        params1.put("vehicleId", "Номер машины");*/
        List<String> params1 = new ArrayList<>();
        reports.add(report);
        Map<String, Object> driverReport = new HashMap<>();
        driverReport.put("id", DriverHoursReport.reportTypeId);
        driverReport.put("name", DriverHoursReport.reportTypeName);
        params1.add("Водитель");
        driverReport.put("parameters", params1);
        reports.add(driverReport);
        return reports;
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report not found"));
    }

    // Другие общие методы
}
