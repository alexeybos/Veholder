package org.skillsmart.veholder.processor;

import lombok.extern.slf4j.Slf4j;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VehicleItemProcessor implements ItemProcessor<Vehicle, Vehicle> {

    private final EnterpriseRepository enterpriseRepository;
    private final BrandRepository brandRepository;

    public VehicleItemProcessor(EnterpriseRepository enterpriseRepository, BrandRepository brandRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.brandRepository = brandRepository;
    }

    @Override
    public Vehicle process(Vehicle vehicle) throws Exception {
        // Здесь можно добавить логику обработки перед сохранением
        // Например, валидацию или преобразование данных
        try {
            if (vehicle.getEnterprise() != null && vehicle.getEnterprise().getId() != null) {
                Enterprise enterprise = enterpriseRepository.findById(vehicle.getEnterprise().getId())
                        .orElseThrow(() -> new RuntimeException("Enterprise not found with id: " + vehicle.getEnterprise().getId()));
                vehicle.setEnterprise(enterprise);
            }
            if (vehicle.getBrand() != null && vehicle.getBrand().getId() != null) {
                Brand brand = brandRepository.findById(vehicle.getBrand().getId())
                        .orElseThrow(() -> new RuntimeException("Brand not found with id: " + vehicle.getBrand().getId()));
                vehicle.setBrand(brand);
            }
        } catch (Exception e) {
            // Логируем ошибку и возвращаем null, чтобы запись была пропущена
            log.error("Ошибка обработки Vehicle: {}", e.getMessage());

            return null; // Spring Batch проигнорирует эту запись
        }

        return vehicle;
    }
}
