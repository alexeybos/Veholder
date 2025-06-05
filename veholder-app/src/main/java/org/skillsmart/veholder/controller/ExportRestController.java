package org.skillsmart.veholder.controller;

import lombok.RequiredArgsConstructor;
//import org.skillsmart.veholder.service.ExportService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
public class ExportRestController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("exportEnterpriseJob")
    private Job exportEnterpriseJob;

    /*@Autowired
    @Qualifier("importEnterpriseJob")
    private Job importEnterpriseJob;*/

    @GetMapping("/export")
    public String exportEnterprises() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(exportEnterpriseJob, jobParameters);
        return "Export job started!";
    }
    /*private final ExportService exportService;

    @PostMapping("/enterprise/{enterpriseId}")
    public ResponseEntity<String> exportEnterprise(
            @PathVariable Long enterpriseId,
            @RequestParam String format) {
        try {
            JobExecution execution = exportService.exportEnterpriseData(enterpriseId, format);
            return ResponseEntity.ok("Export job started with ID: " + execution.getJobId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error starting export job: " + e.getMessage());
        }
    }

    @PostMapping("/vehicles/{enterpriseId}")
    public ResponseEntity<String> exportVehicles(
            @PathVariable Long enterpriseId,
            @RequestParam String format) {
        try {
            JobExecution execution = exportService.exportVehicleData(enterpriseId, format);
            return ResponseEntity.ok("Export job started with ID: " + execution.getJobId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error starting export job: " + e.getMessage());
        }
    }

    @PostMapping("/trips/{vehicleId}")
    public ResponseEntity<String> exportTrips(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String format) {
        try {
            JobExecution execution = exportService.exportTripData(vehicleId, startDate, endDate, format);
            return ResponseEntity.ok("Export job started with ID: " + execution.getJobId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error starting export job: " + e.getMessage());
        }
    }*/
}
