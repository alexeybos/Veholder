package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.Driver;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.dto.DriverDto;
import org.skillsmart.veholder.entity.dto.DriverNoDateDto;
import org.skillsmart.veholder.repository.DriverPagingRepository;
import org.skillsmart.veholder.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository repo;

    @Autowired
    private EnterpriseService enterpriseService;

    public List<DriverDto> getDriversDto() {
        Sort sort = Sort.by("id").ascending();
        return repo.findAll(sort)
                .stream()
                .map(driver -> new DriverDto(driver.getId(), driver.getName(), driver.getBirthDate(),
                        driver.getSalary(), driver.getEnterprise().getId(),
                        Optional.ofNullable(driver.getVehicle()).orElse(new Vehicle()).getId(), driver.isActive()))
                .toList();
    }

    public DriverDto getDriverDTOById(Long id) {
        Driver driver = repo.getReferenceById(id);
        return new DriverDto(driver.getId(), driver.getName(), driver.getBirthDate(), driver.getSalary(),
                driver.getEnterprise().getId(), Optional.ofNullable(driver.getVehicle()).orElse(new Vehicle()).getId(),
                driver.isActive());
    }

    public List<DriverNoDateDto> getDriversDtoByManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return repo.getDriversDTOByManager(username);
        /*return repo.getDriversDTOByManager(username)
                .stream()
                .map(driver -> new DriverDto(driver.getId(), driver.getName(), driver.getBirthDate(),
                        driver.getSalary(), driver.getEnterprise().getId(),
                        Optional.ofNullable(driver.getVehicle()).orElse(new Vehicle()).getId(), driver.isActive()))
                .toList();*/
    }

    public Long createDriver(Driver driver) {
        if (!enterpriseService.checkEnterpriseByManager(driver.getEnterprise().getId())) {
            throw new AccessDeniedException("Можно добавить автомобиль только в свое предприятие!");
        }
        return repo.save(driver).getId();
    }

    public boolean checkDriversByVehicle(Long vehicleId) {
        return repo.getDriversCountOnVehicle(vehicleId) > 0;
    }

    public DriverNoDateDto getDriverByVehicleId(long vehicleId) {
        //Specification<VehicleTrack> spec = Specification.where(byVehicleId(vehicleId))
        //            .and(timeIntervalIn(start, end));
        //        List<VehicleTrack> tracks = repo.findAll(spec);
        //Specification<DriverNoDateDto> spec = Specification.where(byVehicleId(vehicleId));
        return repo.getActiveDriverByVehicle(vehicleId);
    }

}
