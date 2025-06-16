package org.skillsmart.veholder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skillsmart.veholder.entity.Period;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.report.DriverHoursReport;
import org.skillsmart.veholder.repository.ReportRepository;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DriverHoursReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public DriverHoursReport generateDriverHoursReport(Long vehicleId, Period period,
                                                       LocalDateTime startDate, LocalDateTime endDate, String driverName) throws Exception {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        DriverHoursReport report = new DriverHoursReport();
        report.setName("Отчет по водителю (Сидоров) для " + vehicle.getRegistrationNumber());
        report.setPeriod(period);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setVehicle(vehicle);

        Map<String, Map<String, String>> results = calculateDriverHours(vehicleId, period, startDate, endDate);
        /*Map<String, String> finalResult = new HashMap<>();
        results.forEach((month, data) -> {
            finalResult.put(month,
                    "{total_hours: " + data.get("total_hours") +
                            ", Зарплата: " + data.get("salary"));
        });*/
        report.setResults(convertToJsonStringMap(results));
        /*double total = results.values().stream()
                .mapToDouble(value -> ((Number)value).doubleValue())
                .sum();*/

        return reportRepository.save(report);
    }

    public Map<String, String> convertToJsonStringMap(Map<String, Map<String, String>> originalMap) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, Map<String, String>> entry : originalMap.entrySet()) {
            // Сначала преобразуем внутреннюю Map в правильные типы
            Map<String, Object> typedInnerMap = new HashMap<>();

            for (Map.Entry<String, String> innerEntry : entry.getValue().entrySet()) {
                if ("total_hours".equals(innerEntry.getKey())) {
                    typedInnerMap.put(innerEntry.getKey(), Double.parseDouble(innerEntry.getValue()));
                } else {
                    typedInnerMap.put(innerEntry.getKey(), innerEntry.getValue());
                }
            }

            // Затем сериализуем в JSON строку
            String jsonString = objectMapper.writeValueAsString(typedInnerMap);
            result.put(entry.getKey(), jsonString);
        }

        return result;
    }

    private Map<String, Map<String, String>> calculateDriverHours(Long vehicleId, Period period,
                                                 LocalDateTime start, LocalDateTime end) {
        // Реализация расчета пробега по трекам
        // Возвращает Map, где ключ - период (например, "2023-01-01"), значение - пробег в км
        List<Object[]> queryResult = reportRepository.findDriverHours(
                vehicleId,
                start,
                end,
                period
        );

        Map<String, Map<String, String>> result = new HashMap<>();

        for (Object[] row : queryResult) {
            LocalDateTime month = (Timestamp.from((Instant) row[0])).toLocalDateTime();
            String periodKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            if (period == Period.DAY) periodKey = month.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
//            String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            Map<String, String> innerMap = new HashMap<>();
            innerMap.put("total_hours", row[1].toString());
            innerMap.put("salary", row[2].toString());

            result.put(periodKey, innerMap);
        }

        return result;
    }
}
