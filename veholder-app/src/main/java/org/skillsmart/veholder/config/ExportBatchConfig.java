package org.skillsmart.veholder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.skillsmart.veholder.component.BrandIdStepExecutionListener;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.Driver;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.dto.TripDatesDTO;
import org.skillsmart.veholder.entity.dto.TripExportDTO;
import org.skillsmart.veholder.entity.processor.TripToExportDTOProcessor;
import org.skillsmart.veholder.processor.EnterpriseExportItemProcessor;
import org.skillsmart.veholder.repository.*;
import org.skillsmart.veholder.service.BrandIdCollector;
import org.skillsmart.veholder.utils.GUIDWorker;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
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
import java.util.*;

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
    public ItemProcessor<Enterprise, Enterprise> enterpriseProcessor() {
        return new EnterpriseExportItemProcessor();
    }

    @Bean
    public Step exportEnterpriseStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     ItemProcessor<Enterprise, Enterprise> enterpriseProcessor,
                                     RepositoryItemReader<Enterprise> enterpriseExportReader,
                                     FlatFileItemWriter<Enterprise> enterpriseExportWriter) {
        return new StepBuilder("exportEnterpriseStep", jobRepository)
                .<Enterprise, Enterprise>chunk(10, transactionManager)
                .reader(enterpriseExportReader)
                .processor(enterpriseProcessor)
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

    @Bean
    public Job vehicleAndBrandExportJob(JobRepository jobRepository,
                                        Step exportVehiclesStep,
                                        Step exportBrandsStep) {
        return new JobBuilder("vehicleAndBrandExportJob", jobRepository)
                .start(exportVehiclesStep)
                .next(exportBrandsStep)
                .build();
    }

    @Bean
    public Step exportVehiclesStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   VehiclePagingRepository vehicleRepository,
                                   BrandIdCollector brandIdCollector) {
        return new StepBuilder("exportVehiclesStep", jobRepository)
                .<Vehicle, Vehicle>chunk(100, transactionManager)
                .reader(vehicleItemReader(null, null))
                .processor(vehicleProcessor(brandIdCollector))
                .writer(vehicleItemWriter())
                .listener(new BrandIdStepExecutionListener(brandIdCollector))
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Vehicle> vehicleItemReader(
            VehiclePagingRepository repository,
            @Value("#{jobParameters['enterpriseId']}") Long enterpriseId) {
        return new RepositoryItemReaderBuilder<Vehicle>()
                .name("vehicleItemReader")
                .repository(repository)
                .methodName("findByEnterpriseId")
                .arguments(List.of(enterpriseId))
                //.pageSize(100)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Vehicle> vehicleItemWriter() {
        return new FlatFileItemWriterBuilder<Vehicle>()
                .name("vehicleItemWriter")
                .resource(new FileSystemResource("vehicles_export.csv"))
                .delimited()
                .delimiter(",")
                .names("id", "price", "color", "mileage", "registrationNumber", "inOrder", "purchaseDateTime",
                        "brand.id", "enterprise.id")
                .headerCallback(writer -> writer.write("id,price,color,mileage,registrationNumber," +
                        "inOrder,purchaseDateTime,brandId,enterpriseId"))
                .build();
    }

    @Bean
    public ItemProcessor<Vehicle, Vehicle> vehicleProcessor(BrandIdCollector brandIdCollector) {
        return vehicle -> {
            // Собираем ID бренда
            Vehicle newVehicle = new Vehicle(GUIDWorker.getGUID(vehicle.getId()), vehicle);
            newVehicle.setEnterprise(new Enterprise(GUIDWorker.getGUID(vehicle.getEnterprise().getId()), vehicle.getEnterprise()));
            if (vehicle.getBrand() != null) {
                brandIdCollector.addBrandId(vehicle.getBrand().getId());
                newVehicle.setBrand(new Brand(GUIDWorker.getGUID(vehicle.getBrand().getId()), vehicle.getBrand()));
            }
            return newVehicle;
        };
    }

    @Bean
    public Step exportBrandsStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 BrandRepository brandRepository) {
        return new StepBuilder("exportBrandsStep", jobRepository)
                .<Brand, Brand>chunk(100, transactionManager)
                .reader(brandItemReader(null, null))
                .processor(brandItemProcessor())
                .writer(brandItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Brand> brandItemReader(
            BrandRepository repository,
            @Value("#{jobExecutionContext['brandIds']}") Set<Long> brandIds) {

        if (brandIds == null || brandIds.isEmpty()) {
            throw new IllegalStateException("No brand IDs collected from vehicles export");
        }

        return new RepositoryItemReaderBuilder<Brand>()
                .name("brandItemReader")
                .repository(repository)
                .methodName("findAllByIdIn")
                .arguments(List.of(brandIds))
                .pageSize(100)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Brand> brandItemWriter() {
        return new FlatFileItemWriterBuilder<Brand>()
                .name("brandItemWriter")
                .resource(new FileSystemResource("brands_export.csv"))
                .delimited()
                .delimiter(",")
                .names("id", "name", "type", "loadCapacity", "tank", "numberOfSeats")
                .headerCallback(writer -> writer.write("id,name,type,loadCapacity,tank,numberOfSeats"))
                .build();
    }

    @Bean
    public ItemProcessor<Brand, Brand> brandItemProcessor() {
        return brand -> {
            //brand.setId();
            //Brand brnd = new Brand(GUIDWorker.getGUID(brand.getId()), brand);
            return new Brand(GUIDWorker.getGUID(brand.getId()), brand);
        };
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Driver> driverExportReader(
            DriverPagingRepository repository,
            @Value("#{jobParameters['enterpriseId']}") Long enterpriseId) {

        RepositoryItemReader<Driver> reader = new RepositoryItemReader<>();
        reader.setRepository(repository);
        reader.setMethodName("findByEnterpriseId");
        reader.setArguments(List.of(enterpriseId));
        reader.setSort(Map.of("id", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Driver> driverExportWriter() {

        return new FlatFileItemWriterBuilder<Driver>()
                .name("driverExportWriter")
                .resource(new FileSystemResource("drivers_export.csv"))
                .delimited()
                .delimiter(",")
                .names("id", "name", "birthDate", "salary", "enterprise.id",  "isActive", "vehicle.id")
                .headerCallback(writer -> writer.write("id,name,birthDate,salary,enterpriseId,isActive,vehicleId"))
                .build();
    }

    @Bean
    public Step exportDriverStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  RepositoryItemReader<Driver> driverExportReader,
                                  FlatFileItemWriter<Driver> driverExportWriter) {
        return new StepBuilder("exportDriverStep", jobRepository)
                .<Driver, Driver>chunk(10, transactionManager)
                .reader(driverExportReader)
                .processor(driverItemProcessor())
                .writer(driverExportWriter)
                .build();
    }

    @Bean
    public Job exportDriverJob(JobRepository jobRepository,
                                Step exportDriverStep) {
        return new JobBuilder("exportDriverJob", jobRepository)
                .start(exportDriverStep)
                .build();
    }

    @Bean
    public ItemProcessor<Driver, Driver> driverItemProcessor() {
        return driver -> {
            Driver newDriver = new Driver(GUIDWorker.getGUID(driver.getId()), driver);
            if (driver.getVehicle() != null) {
                newDriver.setVehicle(new Vehicle(GUIDWorker.getGUID(driver.getVehicle().getId()), driver.getVehicle()));
            } else newDriver.setVehicle(new Vehicle());
            newDriver.setEnterprise(new Enterprise(GUIDWorker.getGUID(driver.getEnterprise().getId()), driver.getEnterprise()));
            return newDriver;
        };
    }
}
