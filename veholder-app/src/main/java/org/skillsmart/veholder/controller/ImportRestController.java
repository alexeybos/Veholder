package org.skillsmart.veholder.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
public class ImportRestController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("importEnterpriseJob")
    private Job importEnterpriseJob;

    @Autowired
    @Qualifier("importVehicleJob")
    private Job importVehicleJob;

    @Autowired
    @Qualifier("importTripJob")
    private Job importTripJob;

    @GetMapping("/enterprise")
    public String importEnterprises() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(importEnterpriseJob, jobParameters);
        return "Import job started!";
    }

    @GetMapping("/vehicle")
    public String importVehicles(@RequestParam(defaultValue = "csv") String format) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .addString("format", format)
                .toJobParameters();
        jobLauncher.run(importVehicleJob, jobParameters);
        return "Import job started!";
    }

    @GetMapping("/trip")
    public String importTrips(@RequestParam(defaultValue = "csv") String format) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .addString("format", format)
                .addString("inputFile", "trip_import.csv")
                .toJobParameters();
        jobLauncher.run(importTripJob, jobParameters);
        return "Import job started!";
    }

}
