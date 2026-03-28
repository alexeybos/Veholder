package org.skillsmart.veholder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleProjection;
import org.skillsmart.veholder.entity.dto.StatisticEvent;
import org.skillsmart.veholder.entity.dto.VehicleDTO;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.repository.VehiclePagingRepository;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.skillsmart.veholder.entity.dto.VehicleEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class VehicleService {

    @Autowired
    VehicleRepository repo;
    @Autowired
    VehiclePagingRepository pageRepo;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private final KafkaTemplate<String, VehicleEvent> kafkaTemplate;
    @Autowired
    private final KafkaTemplate<String, StatisticEvent> kafkaTemplateStatistic;

    public VehicleService(KafkaTemplate<String, VehicleEvent> kafkaTemplate, KafkaTemplate<String, StatisticEvent> kafkaTemplateStatistic) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplateStatistic = kafkaTemplateStatistic;
    }

    public void save(Vehicle vehicle) {
        repo.save(vehicle);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Vehicle> getList(Sort sortBy) {
        return repo.findAll(sortBy);

    }

    public List<VehicleProjection> getOnlyVehiclesList(Sort sortBy) {
        return repo.findAllProjectedBy(sortBy);
    }

    public List<VehicleProjection> getOnlyVehiclesListForManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return repo.getVehiclesByManager(username);
    }

    public VehicleProjection getVehicleProjectedById(Long id) {
        return repo.getReferenceProjectedById(id);
    }

    public Vehicle getVehicleById(Long id) {
        return repo.getReferenceById(id);
    }

    public Long createVehicle(Vehicle vehicle) {
        /*if (!enterpriseService.checkEnterpriseByManager(vehicle.getEnterprise().getId())) {
            throw new AccessDeniedException("Можно добавить автомобиль только в свое предприятие!");
        }
        // Отправка события в Kafka
        VehicleEvent event = new VehicleEvent(
                VehicleEvent.EventType.VEHICLE_CREATED,
                vehicle.getId(),
                vehicle.getEnterprise().getId(),
                enterpriseService.getManagerName(),
                vehicle.getRegistrationNumber(),
                LocalDateTime.now(),
                null
        );

        kafkaTemplate.send("vehicle-events", event);
        StatisticEvent statisticEvent = new StatisticEvent("vehicle", "update", LocalDateTime.now());
        kafkaTemplateStatistic.send("veholder-stats", statisticEvent);*/
        return repo.save(vehicle).getId();
    }

    public void updateVehicle(Long id, Map<String, Object> values) {
        Vehicle vehicle = repo.getReferenceById(id);
        Long enterpriseId = vehicle.getEnterprise().getId();
        /*if (!enterpriseService.checkEnterpriseByManager(enterpriseId)) {
            throw new AccessDeniedException("Можно редактировать автомобиль только в своем предприятии!");
        }
        if (!enterpriseService.checkEnterpriseByManager(Long.parseLong(values.getOrDefault("enterpriseId", enterpriseId).toString()))) {
            throw new AccessDeniedException("Можно редактировать автомобиль только в своем предприятии!");
        }*/

        Map<String, Object> changes = new HashMap<>();
        changes.put("color", Map.of("old", vehicle.getColor(), "new", values.getOrDefault("color", vehicle.getColor())));
        //vehicle.getBrand().setId((Long) values.getOrDefault("brandId", vehicle.getBrand().getId()));
        //vehicle.getBrand().setId(Long.parseLong(values.getOrDefault("brandId", vehicle.getBrand().getId()).toString()));
        if (values.containsKey("brandId")) {
            Long newBrandId = Long.parseLong(values.get("brandId").toString());

            // Загружаем новый Brand из базы
            Brand newBrand = brandRepository.findById(newBrandId)
                    .orElseThrow(() -> new EntityNotFoundException("Brand not found with id: " + newBrandId));
            // Устанавливаем новый Brand
            vehicle.setBrand(newBrand);
        }

        vehicle.setColor((String) values.getOrDefault("color", vehicle.getColor()));
        vehicle.getEnterprise().setId(Long.parseLong(values.getOrDefault("enterpriseId", vehicle.getEnterprise().getId()).toString()));
        vehicle.setMileage(Integer.parseInt(values.getOrDefault("mileage", vehicle.getMileage()).toString()));
        vehicle.setInOrder((Boolean) values.getOrDefault("inOrder", vehicle.isInOrder()));
        vehicle.setPrice(Double.parseDouble(values.getOrDefault("price", vehicle.getPrice()).toString()));
        vehicle.setRegistrationNumber((String) values.getOrDefault("registrationNumber", vehicle.getRegistrationNumber()));
        vehicle.setYearOfProduction(Integer.parseInt(values.getOrDefault("yearOfProduction", vehicle.getYearOfProduction()).toString()));
        //vehicle.setPurchaseDateTime(Instant.parse(values.getOrDefault("purchaseDateTime", vehicle.getPurchaseDateTime()).toString()));

        repo.save(vehicle);
        repo.flush();


        /*VehicleEvent event = new VehicleEvent(
                VehicleEvent.EventType.VEHICLE_UPDATED,
                vehicle.getId(),
                vehicle.getEnterprise().getId(),
                enterpriseService.getManagerName(),
                vehicle.getRegistrationNumber(),
                LocalDateTime.now(),
                changes
        );

        kafkaTemplate.send("vehicle-events", event);
        StatisticEvent statisticEvent = new StatisticEvent("vehicle", "create", LocalDateTime.now());
        kafkaTemplateStatistic.send("veholder-stats", statisticEvent);*/

    }

    public void deleteVehicle(Long id) {
        Vehicle vehicle = repo.getReferenceById(id);
        Long enterpriseId = vehicle.getEnterprise().getId();
        if (!enterpriseService.checkEnterpriseByManager(enterpriseId)) {
            throw new AccessDeniedException("Можно удалить автомобиль только из своего предприятия!");
        }
        if (driverService.checkDriversByVehicle(id)) {
            throw new AccessDeniedException("К машине привязан(ы) водители. Невозможно удалить автомобиль!");
        }
        repo.deleteById(id);
        repo.flush();
    }

    public Page<VehicleProjection> getPagingVehicles(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return pageRepo.getVehiclesByManagerPaging(pageable, username);
    }

    //@Cacheable("vehicles")
    public Page<VehicleDTO> getPagingVehiclesByEnterprise(Pageable pageable, Long enterpriseId) {
        //log.info("getting {} page of vehicles", pageable.getPageNumber());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "man3";
        if (authentication != null) {
            username = authentication.getName();
        }
        return pageRepo.getVehiclesByEnterprise(pageable, enterpriseId, username);
    }

    /*public Mono<Page<VehicleDTO>> getPagingVehiclesByEnterprise(Pageable pageable, Long enterpriseId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .flatMap(username -> {
                    log.info("getting {} page of vehicles for user: {}", pageable.getPageNumber(), username);

                    // Получаем данные страницы
                    Mono<Page<VehicleDTO>> vehiclesPage = pageRepo
                            .getVehiclesByEnterprise(pageable, enterpriseId, username);

                    // Если нужен общий счетчик, можно добавить:
                    //Mono<Long> totalCount = pageRepo.countVehiclesByEnterprise(enterpriseId, username);

                    return vehiclesPage;
                })
                .switchIfEmpty(Mono.error(new RuntimeException("User not authenticated")));
    }*/

}
