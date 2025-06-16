package org.skillsmart.veholder.service;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Компонент для сбора ID брендов во время экспорта транспортных средств.
 * Сохраняет ID в контексте выполнения Job для последующего использования.
 */
@Component
@StepScope
public class BrandIdCollector implements StepExecutionListener {

    private Set<Long> brandIds;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.brandIds = new HashSet<>();
    }

    public void addBrandId(Long brandId) {
        if (brandId != null) {
            this.brandIds.add(brandId);
        }
    }

    public Set<Long> getBrandIds() {
        return Collections.unmodifiableSet(brandIds);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // В этом случае логирование перенесено в BrandIdStepExecutionListener
        return ExitStatus.COMPLETED;
    }
}