package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.report.Report;
import org.skillsmart.veholder.repository.ReportRepository;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report not found"));
    }

    // Другие общие методы
}
