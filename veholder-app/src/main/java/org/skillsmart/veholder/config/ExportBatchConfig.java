package org.skillsmart.veholder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.dto.TripDatesDTO;
import org.skillsmart.veholder.entity.dto.TripExportDTO;
import org.skillsmart.veholder.entity.processor.TripToExportDTOProcessor;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.repository.TripRepository;
import org.skillsmart.veholder.repository.VehiclePagingRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class ExportBatchConfig {

    @Bean
    public RepositoryItemReader<Enterprise> enterpriseExportReader(EnterpriseRepository repository) {
        RepositoryItemReader<Enterprise> reader = new RepositoryItemReader<>();
        reader.setRepository(repository);
        reader.setMethodName("findAll");
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public FlatFileItemWriter<Enterprise> enterpriseExportWriter() {
        return new FlatFileItemWriterBuilder<Enterprise>()
                .name("enterpriseExportWriter")
                .resource(new FileSystemResource("enterprises_export.csv"))
                .delimited()
                .delimiter(",")
                .names("id", "name", "city", "directorName", "timezone")
                .build();
    }

    @Bean
    public Step exportEnterpriseStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     RepositoryItemReader<Enterprise> enterpriseExportReader,
                                     FlatFileItemWriter<Enterprise> enterpriseExportWriter) {
        return new StepBuilder("exportEnterpriseStep", jobRepository)
                .<Enterprise, Enterprise>chunk(10, transactionManager)
                .reader(enterpriseExportReader)
                .writer(enterpriseExportWriter)
                .build();
    }

    @Bean
    public Job exportEnterpriseJob(JobRepository jobRepository,
                                   Step exportEnterpriseStep) {
        return new JobBuilder("exportEnterpriseJob", jobRepository)
                .start(exportEnterpriseStep)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Vehicle> vehicleExportReader(
            VehiclePagingRepository repository,
            @Value("#{jobParameters['enterpriseId']}") Long enterpriseId) {

        RepositoryItemReader<Vehicle> reader = new RepositoryItemReader<>();
        reader.setRepository(repository);
        reader.setMethodName("findByEnterpriseId");
        reader.setArguments(List.of(enterpriseId));
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Vehicle> vehicleExportWriter(
            @Value("#{jobParameters['outputFile']}") String outputFile) {

        return new FlatFileItemWriterBuilder<Vehicle>()
                .name("vehicleExportWriter")
                .resource(new FileSystemResource(outputFile))
                .delimited()
                .delimiter(",")
                .names("id", "price", "color", "mileage", "registrationNumber", "inOrder", "purchaseDateTime",
                        "brand.id", "enterprise.id")
                .build();
    }

    @Bean
    public Step exportVehicleStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  RepositoryItemReader<Vehicle> vehicleExportReader,
                                  FlatFileItemWriter<Vehicle> vehicleExportWriter) {
        return new StepBuilder("exportVehicleStep", jobRepository)
                .<Vehicle, Vehicle>chunk(10, transactionManager)
                .reader(vehicleExportReader)
                .writer(vehicleExportWriter)
                .build();
    }

    @Bean
    public Job exportVehicleJob(JobRepository jobRepository,
                                Step exportVehicleStep) {
        return new JobBuilder("exportVehicleJob", jobRepository)
                .start(exportVehicleStep)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<TripDatesDTO> tripExportReader(
            TripRepository repository,
            @Value("#{jobParameters['vehicleId']}") Long vehicleId,
            @Value("#{jobParameters['startDate']}") ZonedDateTime startDate,
            @Value("#{jobParameters['endDate']}") ZonedDateTime endDate) {

        RepositoryItemReader<TripDatesDTO> reader = new RepositoryItemReader<>();
        reader.setRepository(repository);
        reader.setMethodName("findTripsNativePage");
        reader.setArguments(Arrays.asList(vehicleId, startDate, endDate));
        reader.setPageSize(100);
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public ItemProcessor<TripDatesDTO, TripExportDTO> tripProcessor() {
        return new TripToExportDTOProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<TripExportDTO> tripExportWriter(
            @Value("#{jobParameters['outputFile']}") String outputFile) {

        return new FlatFileItemWriterBuilder<TripExportDTO>()
                .name("tripExportWriter")
                .resource(new FileSystemResource(outputFile))
                .delimited()
                .delimiter(",")
                .names("id", "vehicleId", "startInterval", "endInterval")
                .build();
    }

    @Bean
    @StepScope
    public CompositeItemWriter<TripExportDTO> tripWriter(
            @Value("#{jobParameters['outputFile']}") String outputFile,
            @Value("#{jobParameters['format']}") String format) {

        CompositeItemWriter<TripExportDTO> writer = new CompositeItemWriter<>();

        if ("csv".equalsIgnoreCase(format)) {
            writer.setDelegates(List.of(tripExportWriter(outputFile)));
        } else if ("json".equalsIgnoreCase(format)) {
            writer.setDelegates(List.of(tripJsonWriter(outputFile)));
        } else {
            writer.setDelegates(List.of(tripExportWriter(outputFile)));
        }

        return writer;
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<TripExportDTO> tripJsonWriter(
            @Value("#{jobParameters['outputFile']}") String outputFile
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new JsonFileItemWriterBuilder<TripExportDTO>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>(objectMapper))
                .resource(new FileSystemResource(outputFile))
                .name("tripJsonWriter")
                .build();
    }

    @Bean
    public Step exportTripStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               ItemProcessor<TripDatesDTO, TripExportDTO> tripProcessor,
                               RepositoryItemReader<TripDatesDTO> tripExportReader,
                               CompositeItemWriter<TripExportDTO> tripWriter) {
        return new StepBuilder("exportTripStep", jobRepository)
                .<TripDatesDTO, TripExportDTO>chunk(10, transactionManager)
                .reader(tripExportReader)
                .processor(tripProcessor)
                .writer(tripWriter)
                .build();
    }

    @Bean
    public Job exportTripJob(JobRepository jobRepository,
                             Step exportTripStep) {
        return new JobBuilder("exportTripJob", jobRepository)
                .start(exportTripStep)
                .build();
    }

}
