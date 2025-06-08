package org.skillsmart.veholder.processor;

import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.utils.GUIDWorker;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EnterpriseExportItemProcessor implements ItemProcessor<Enterprise, Enterprise> {

    @Override
    public Enterprise process(Enterprise enterprise) throws Exception {
        // Здесь можно добавить логику обработки перед сохранением
        // Например, валидацию или преобразование данных
        enterprise.setId(GUIDWorker.getGUID(enterprise.getId()));
        return enterprise;
    }
}
