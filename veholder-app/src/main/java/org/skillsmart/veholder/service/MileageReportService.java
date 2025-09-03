package org.skillsmart.veholder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skillsmart.veholder.entity.Period;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.report.MileageReport;
import org.skillsmart.veholder.repository.ReportRepository;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MileageReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private VehicleRepository vehicleRepository;


    public MileageReport generateMileageReport(Long vehicleId, Period period,
                                               LocalDateTime startDate, LocalDateTime endDate) throws JsonProcessingException {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        MileageReport report = new MileageReport();
        report.setName("Отчет по пробегу для " + vehicle.getRegistrationNumber());
        report.setPeriod(period);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setVehicle(vehicle);

        Map<String, Object> mileageData = calculateMileage(vehicleId, period, startDate, endDate);
        report.setResults(mileageData.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() != null ? entry.getValue().toString() : null
                )));

        double total = mileageData.values().stream()
                .mapToDouble(value -> ((Number)value).doubleValue())
                .sum();
        report.setTotalMileage(total);

        return reportRepository.save(report);
    }

    private Map<String, Object> calculateMileage(Long vehicleId, Period period,
                                                 LocalDateTime start, LocalDateTime end) {
        // Реализация расчета пробега по трекам
        // Возвращает Map, где ключ - период (например, "2023-01-01"), значение - пробег в км
        List<Object[]> results = reportRepository.findVehicleMileageByPeriod(
                vehicleId,
                start,
                end,
                period.toString()
        );

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],    // period_name
                        row -> (Double) row[1],    // period_distance
                        (a, b) -> a,               // merge function
                        LinkedHashMap::new         // сохраняем порядок
                ));
    }

    public MileageReport getMileageReport(Map<String, Object> params) throws JsonProcessingException {
        Long vehicleId = (Long) params.get("vehicleId");
        Period period = (Period) params.get("period");
        //DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDateTime startDate = LocalDateTime.parse((String) params.get("startDate"), formatter);
        LocalDateTime endDate = LocalDateTime.parse((String) params.get("endDate"), formatter);
        // сначала ищем в таблице отчет точно по имеющимся параметрам

        // если не нашли - преобразовываем параметры и генерируем
        return generateMileageReport(vehicleId, period, startDate, endDate);
    }

}
