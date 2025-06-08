package org.skillsmart.veholder.processor;

import lombok.extern.slf4j.Slf4j;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.utils.GUIDWorker;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VehicleExportItemProcessor implements ItemProcessor<Vehicle, Vehicle> {

    @Override
    public Vehicle process(Vehicle vehicle) throws Exception {
        // Здесь можно добавить логику обработки перед сохранением
        // Например, валидацию или преобразование данных
        vehicle.setId(GUIDWorker.getGUID(vehicle.getId()));
        vehicle.getEnterprise().setId(GUIDWorker.getGUID(vehicle.getEnterprise().getId()));
        vehicle.getBrand().setId(GUIDWorker.getGUID(vehicle.getBrand().getId()));

        return vehicle;
    }
}
