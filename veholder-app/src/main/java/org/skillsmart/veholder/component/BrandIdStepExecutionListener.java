package org.skillsmart.veholder.component;

import lombok.extern.slf4j.Slf4j;
import org.skillsmart.veholder.service.BrandIdCollector;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Слушатель шага экспорта транспортных средств, который:
 * 1. Инициализирует коллектор ID брендов перед началом шага
 * 2. Сохраняет собранные ID в контекст выполнения Job
 */
@Component
@StepScope
@Slf4j
public class BrandIdStepExecutionListener implements StepExecutionListener {

    private final BrandIdCollector brandIdCollector;

    public BrandIdStepExecutionListener(BrandIdCollector brandIdCollector) {
        this.brandIdCollector = brandIdCollector;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // Инициализируем коллектор перед началом шага
        brandIdCollector.beforeStep(stepExecution);

        // Логирование начала шага
        stepExecution.getExecutionContext().putString("stepStatus", "IN_PROGRESS");
        log.info("Starting vehicle export step. Job parameters: {}",
                stepExecution.getJobParameters());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Получаем собранные ID брендов
        Set<Long> brandIds = brandIdCollector.getBrandIds();

        // Сохраняем в контекст выполнения Job для использования в следующем шаге
        stepExecution.getJobExecution()
                .getExecutionContext()
                .put("brandIds", brandIds);

        // Логирование результатов
        log.info("Vehicle export step completed. Exported {} vehicles, collected {} unique brand IDs",
                stepExecution.getWriteCount(), brandIds.size());

        // Добавляем дополнительную информацию в ExitStatus
        ExitStatus exitStatus = new ExitStatus("COMPLETED",
                String.format("Exported %d vehicles with %d brands",
                        stepExecution.getWriteCount(), brandIds.size()));

        return exitStatus;
    }
}