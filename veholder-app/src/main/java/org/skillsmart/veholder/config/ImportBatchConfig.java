package org.skillsmart.veholder.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.skillsmart.veholder.component.TripImportWriter;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skillsmart.veholder.entity.dto.TripExportDTO;
import org.skillsmart.veholder.entity.dto.TripImportResult;
import org.skillsmart.veholder.processor.EnterpriseItemProcessor;
import org.skillsmart.veholder.processor.TripImportProcessor;
import org.skillsmart.veholder.processor.VehicleItemProcessor;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class ImportBatchConfig {

    @Bean
    public FlatFileItemReader<Enterprise> enterpriseImportReader() {
        return new FlatFileItemReaderBuilder<Enterprise>()
                .name("enterpriseImportReader")
                .resource(new FileSystemResource("enterprises_import.csv"))
                .delimited()
                .names("name", "city", "directorName", "timezone")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Enterprise.class);
                }})
                .build();
    }

    @Bean
    public EnterpriseItemProcessor enterpriseItemProcessor() {
        return new EnterpriseItemProcessor();
    }

    @Bean
    public Step importEnterpriseStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     FlatFileItemReader<Enterprise> enterpriseImportReader,
                                     EnterpriseItemProcessor enterpriseItemProcessor,
                                     EnterpriseRepository enterpriseRepository) {
        return new StepBuilder("importEnterpriseStep", jobRepository)
                .<Enterprise, Enterprise>chunk(10, transactionManager)
                .reader(enterpriseImportReader)
                .processor(enterpriseItemProcessor)
                .writer(enterpriseRepository::saveAll)
                .build();
    }

    @Bean
    public Job importEnterpriseJob(JobRepository jobRepository,
                                   Step importEnterpriseStep) {
        return new JobBuilder("importEnterpriseJob", jobRepository)
                .start(importEnterpriseStep)
                .build();
    }

    @Bean
    public FlatFileItemReader<Vehicle> vehicleImportCsvReader() {
        return new FlatFileItemReaderBuilder<Vehicle>()
                .name("vehicleImportCsvReader")
                .resource(new FileSystemResource("vehicles_import.csv"))
                .delimited()
                .names("price", "color", "mileage", "registrationNumber", "inOrder", "purchaseDateTime",
                        "brand.id", "enterprise.id")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Vehicle.class);
                }})
                .build();
    }

    @Bean
    public JsonItemReader<Vehicle> vehicleJsonReader() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Регистрируем JavaTimeModule и настраиваем обработку дат
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);

        // Используем ISO-8601 формат вместо timestamp
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

        // Создаем JsonObjectReader с настроенным ObjectMapper
        JacksonJsonObjectReader<Vehicle> jsonObjectReader = new JacksonJsonObjectReader<>(Vehicle.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<Vehicle>()
                .jsonObjectReader(jsonObjectReader)
                .resource(new FileSystemResource("vehicles_import.json"))
                .name("vehicleJsonReader")
                .build();
    }

    @Bean
    public VehicleItemProcessor vehicleItemProcessor(EnterpriseRepository enterpriseRepository,
                                                     BrandRepository brandRepository) {
        return new VehicleItemProcessor(enterpriseRepository, brandRepository);
    }

    @Bean
    public Step importVehicleStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     CompositeItemReader<Vehicle> vehicleCompositeItemReader,
                                     VehicleItemProcessor vehicleItemProcessor,
                                     VehicleRepository vehicleRepository) {
        return new StepBuilder("importVehicleStep", jobRepository)
                .<Vehicle, Vehicle>chunk(10, transactionManager)
                .reader(vehicleCompositeItemReader)
                .processor(vehicleItemProcessor)
                .writer(vehicleRepository::saveAll)
                .build();
    }

    @Bean
    public Job importVehicleJob(JobRepository jobRepository,
                                   Step importVehicleStep) {
        return new JobBuilder("importVehicleJob", jobRepository)
                .start(importVehicleStep)
                .build();
    }

    @Bean
    @StepScope
    public CompositeItemReader<Vehicle> vehicleCompositeItemReader(
            @Value("#{jobParameters['format']}") String format) {
        CompositeItemReader<Vehicle> reader = null;

        if ("csv".equalsIgnoreCase(format)) {
            reader = new CompositeItemReader<>(List.of(vehicleImportCsvReader()));
        } else if ("json".equalsIgnoreCase(format)) {
            reader = new CompositeItemReader<>(List.of(vehicleJsonReader()));
        } else {
            reader = new CompositeItemReader<>(List.of(vehicleImportCsvReader()));
        }
        return reader;
    }

    //@Bean
    //public FlatFileItemReader<Enterprise> enterpriseCsvReader() {
    //    return new FlatFileItemReaderBuilder<Enterprise>()
    //        .name("enterpriseCsvReader")
    //        .resource(new FileSystemResource("import/enterprise.csv"))
    //        .delimited()
    //        .names(new String[]{"id", "name"}) // поля должны соответствовать CSV
    //        .fieldSetMapper(new BeanWrapperFieldSetMapper<Enterprise>() {{
    //            setTargetType(Enterprise.class);
    //        }})
    //        .build();
    //}
    //

    //
    //@Bean
    //public JpaItemWriter<Enterprise> enterpriseDbWriter(EntityManagerFactory entityManagerFactory) {
    //    return new JpaItemWriterBuilder<Enterprise>()
    //        .entityManagerFactory(entityManagerFactory)
    //        .build();
    //}

    @Bean
    @StepScope
    public FlatFileItemReader<TripExportDTO> tripImportReader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {

        return new FlatFileItemReaderBuilder<TripExportDTO>()
                .name("tripImportReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .delimiter(",")
                .names(new String[]{
                        "vehicleId", "tripStart", "tripEnd", "startLon", "startLat", "endLon", "endLat"
                })
                .fieldSetMapper(new TripDescriptionFieldSetMapper()) // Используем наш маппер
                /*.fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(TripExportDTO.class);
                }})*/
                .build();
    }

    @Bean
    public Step importTripStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               FlatFileItemReader<TripExportDTO> tripImportReader,
                               TripImportProcessor tripImportProcessor,
                               TripImportWriter tripImportWriter) {
        return new StepBuilder("importTripStep", jobRepository)
                .<TripExportDTO, TripImportResult>chunk(10, transactionManager)
                .reader(tripImportReader)
                .processor(tripImportProcessor)
                .writer(tripImportWriter)
                .build();
    }

    @Bean
    public Job importTripJob(JobRepository jobRepository,
                             Step importTripStep) {
        return new JobBuilder("importTripJob", jobRepository)
                .start(importTripStep)
                .build();
    }
}
