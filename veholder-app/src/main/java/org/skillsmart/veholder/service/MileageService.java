package org.skillsmart.veholder.service;

import jakarta.persistence.EntityNotFoundException;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.dto.EnterpriseMileageResponse;
import org.skillsmart.veholder.entity.dto.MileageResponse;
import org.skillsmart.veholder.repository.DriverRepository;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.repository.VehicleTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MileageService {

    @Autowired
    private VehicleTrackRepository repo;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private DriverRepository driverRepository;

    public Double getVehicleMileage(Long vehicleId, String dateString) {
        LocalDate date = parseDate(dateString);

        if (dateString.split("\\.").length == 2) {
            // mm.yyyy format - monthly mileage
            return repo.findMonthlyMileageByVehicleId(vehicleId, date);
        } else {
            // dd.mm.yyyy format - daily mileage
            return repo.findDailyMileageByVehicleId(vehicleId, date);
        }
    }

    public EnterpriseMileageResponse getEnterpriseMileage(Long enterpriseId, String dateString) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new EntityNotFoundException("Enterprise not found"));

        LocalDate date = parseDate(dateString);
        List<Object[]> results;
        String period;

        if (dateString.split("\\.").length == 2) {
            // mm.yyyy format - monthly mileage
            results = repo.findMonthlyMileageByEnterpriseId(enterpriseId, date);
            period = date.format(DateTimeFormatter.ofPattern("MM.yyyy"));
        } else {
            // dd.mm.yyyy format - daily mileage
            results = repo.findDailyMileageByEnterpriseId(enterpriseId, date);
            period = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }

        List<MileageResponse> vehicleMileages = results.stream()
                .map(result -> new MileageResponse(
                        ((Number) result[0]).longValue(),
                        (String) result[1],
                        ((Number) result[3]).doubleValue(),
                        period,
                        (String) result[2]
                ))
                .collect(Collectors.toList());

        Double totalMileage = vehicleMileages.stream()
                .mapToDouble(MileageResponse::getTotalMileage)
                .sum();

        return new EnterpriseMileageResponse(
                enterpriseId,
                enterprise.getName(),
                vehicleMileages,
                totalMileage,
                period
        );
    }

    private LocalDate parseDate(String dateString) {
        String[] parts = dateString.split("\\.");
        if (parts.length == 2) {
            // mm.yyyy
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            return LocalDate.of(year, month, 1);
        } else if (parts.length == 3) {
            // dd.mm.yyyy
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            return LocalDate.of(year, month, day);
        } else {
            throw new IllegalArgumentException("Invalid date format. Use dd.mm.yyyy or mm.yyyy");
        }
    }
}
