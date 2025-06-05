package org.skillsmart.veholder.config;

import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

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

}
