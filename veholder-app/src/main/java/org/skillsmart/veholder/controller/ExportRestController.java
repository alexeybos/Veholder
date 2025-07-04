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
import java.time.ZonedDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/api/export")
public class ExportRestController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("exportEnterpriseJob")
    private Job exportEnterpriseJob;

    @Autowired
    @Qualifier("exportVehicleJob")
    private Job exportVehicleJob;

    @Autowired
    @Qualifier("exportTripJob")
    private Job exportTripJob;

    @Autowired
    @Qualifier("vehicleAndBrandExportJob")
    private Job vehicleAndBrandExportJob;

    @Autowired
    @Qualifier("exportDriverJob")
    private Job exportDriverJob;


    @GetMapping("/enterprise")
    public String exportEnterprises() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(exportEnterpriseJob, jobParameters);
        return "Export job started!";
    }

    @GetMapping("/vehicles/{enterpriseId}")
    public String exportVehiclesByEnterprise(@PathVariable Long enterpriseId) throws Exception {
        //TODO Проверка на принадлежность и наличие предприятия менеджеру
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("enterpriseId", enterpriseId)
                .addString("outputFile", "vehicles_enterprise_" + enterpriseId + "_export.csv")
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(exportVehicleJob, jobParameters);
        return "Vehicle export job for enterprise " + enterpriseId + " started!";
    }

    @GetMapping("/trips/{vehicleId}")
    public ResponseEntity<String> exportTripsByVehicleAndDate(
            @PathVariable Long vehicleId,
            @RequestParam(required = false) ZonedDateTime startDate,
            @RequestParam(required = false) ZonedDateTime endDate,
            @RequestParam(defaultValue = "csv") String format) {

        try {
            String outputFile =
                    String.format("trips_vehicle_%d_%s_%s.%s",
                            vehicleId,
                            startDate != null ? startDate.toString() : "all",
                            endDate != null ? endDate.toString() : "all",
                            Objects.equals(format, "json") ? "json" : "csv");

            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                    .addLong("vehicleId", vehicleId)
                    .addString("outputFile", outputFile)
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("format", format);

            if (startDate != null) {
                jobParametersBuilder.addJobParameter("startDate", startDate, ZonedDateTime.class);
            }
            if (endDate != null) {
                jobParametersBuilder.addJobParameter("endDate", endDate, ZonedDateTime.class);
            }

            JobExecution jobExecution = jobLauncher.run(exportTripJob, jobParametersBuilder.toJobParameters());

            return ResponseEntity.accepted()
                    .body(String.format("Export started. JobId: %s. File: %s",
                            jobExecution.getJobId(), outputFile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error starting export: " + e.getMessage());
        }
    }

    @GetMapping("/enterprise/full/{id}")
    public String exportEnterpriseFull(@PathVariable Long id) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(exportEnterpriseJob, jobParameters);

        JobParameters vehicleJobParameters = new JobParametersBuilder()
                .addLong("enterpriseId", id)
                .addString("outputFile", "vehicles_enterprise_" + id + "_export.csv")
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(vehicleAndBrandExportJob, vehicleJobParameters);

        JobParameters driverJobParameters = new JobParametersBuilder()
                .addLong("enterpriseId", id)
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(exportDriverJob, driverJobParameters);
        //exportDriverJob

        return "Full enterprise export started! Started enterprise -> drivers -> vehicles -> brands -> trips export jobs.";
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
