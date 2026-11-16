package org.skillsmart.veholder.service;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import jakarta.persistence.EntityNotFoundException;
import org.mockito.Mockito;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.dto.EnterpriseMileageResponse;
import org.skillsmart.veholder.repository.DriverRepository;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.repository.VehicleTrackRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FuzzMileageServiceTest {

    //private final MileageService service = createService();

    private MileageService service;
    private VehicleTrackRepository repo;
    private EnterpriseRepository enterpriseRepo;
    private DriverRepository driverRepo;
    private void setup() {
        service = new MileageService();
        repo = mock(VehicleTrackRepository.class);
        enterpriseRepo = mock(EnterpriseRepository.class);
        driverRepo = mock(DriverRepository.class);
        inject(service, "repo", repo);
        inject(service, "enterpriseRepository", enterpriseRepo);
        inject(service, "driverRepository", driverRepo);
    }
    private static MileageService createService() {
        MileageService s = new MileageService();
        inject(s, "repo", mock(VehicleTrackRepository.class));
        inject(s, "enterpriseRepository", mock(EnterpriseRepository.class));
        inject(s, "driverRepository", mock(DriverRepository.class));
        return s;
    }
    private static void inject(Object target, String field, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FuzzTest(maxDuration = "3m")
    void fuzzStructuredDate(FuzzedDataProvider data) {
        long vehicleId = data.consumeLong();

        // выбираем «режим» генерации
        int mode = data.consumeInt(0, 4);
        String dateString;
        switch (mode) {
            case 0: // валидный dd.mm.yyyy
                dateString = data.consumeInt(1, 31) + "." + data.consumeInt(1, 12) + "." + data.consumeInt(1900, 2100);
                break;
            case 1: // валидный mm.yyyy
                dateString = data.consumeInt(1, 12) + "." + data.consumeInt(1900, 2100);
                break;
            case 2: // граничные числа
                dateString = data.consumeInt(-100, 100) + "." + data.consumeInt(-100, 100) + "." + data.consumeInt(-10000, 10000);
                break;
            case 3: // произвольный мусор
                dateString = data.consumeAsciiString(30);
                break;
            default: // null
                dateString = null;
        }
        try {
            service.getVehicleMileage(vehicleId, dateString);
        } catch (IllegalArgumentException | jakarta.persistence.EntityNotFoundException e) {
            // ожидаемо
        }
// NPE, DateTimeException, NumberFormatException — упадут и будут зафиксированы
    }

    @FuzzTest(maxDuration = "3m")
    void fuzzGetEnterpriseMileage(FuzzedDataProvider data) {
        setup();
        // 1. Генерируем вход метода
        long enterpriseId = data.consumeLong(-10, 1_000_000);
        String dateString = buildDateString(data);
        // 2. Решаем, найдётся ли Enterprise
        boolean enterpriseExists = data.consumeBoolean();
        if (enterpriseExists) {
            Enterprise e = new Enterprise();
            e.setId(enterpriseId);
            // имя может быть null, пустым или произвольным
            e.setName(data.consumeBoolean() ? null : data.consumeAsciiString(20));
            when(enterpriseRepo.findById(enterpriseId)).thenReturn(Optional.of(e));
        } else {
            when(enterpriseRepo.findById(enterpriseId)).thenReturn(Optional.empty());
        }
        // 3. Генерируем «ответ» БД — список Object[] переменной структуры
        int rowCount = data.consumeInt(0, 5);
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            rows.add(buildRow(data));
        }
        when(repo.findMonthlyMileageByEnterpriseId(anyLong(), any())).thenReturn(rows);
        when(repo.findDailyMileageByEnterpriseId(anyLong(), any())).thenReturn(rows);
        // 4. Вызов и фильтрация ожидаемых исключений
        try {
            EnterpriseMileageResponse resp = service.getEnterpriseMileage(enterpriseId, dateString);
            // дополнительные инварианты — если метод вернул результат, он должен быть консистентным
            if (resp != null && Double.isNaN(resp.getTotalMileage())) {
                throw new AssertionError("totalMileage is NaN — downstream will break");
            }
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            // ожидаемые бизнес-исключения
        }
        // ClassCastException, NPE, AIOOBE — пролетят наружу и будут зафиксированы
    }
    private String buildDateString(FuzzedDataProvider data) {
        int mode = data.consumeInt(0, 3);
        switch (mode) {
            case 0: return data.consumeInt(1, 31) + "." + data.consumeInt(1, 12) + "." + data.consumeInt(1900, 2100);
            case 1: return data.consumeInt(1, 12) + "." + data.consumeInt(1900, 2100);
            case 2: return data.consumeAsciiString(20);
            default: return null;
        }
    }
    // Генерация «сырой» строки результата БД
    private Object[] buildRow(FuzzedDataProvider data) {
        int len = data.consumeInt(0, 6);
        Object[] row = new Object[len];
        for (int i = 0; i < len; i++) {
            row[i] = buildCell(data);
        }
        return row;
    }
    private Object buildCell(FuzzedDataProvider data) {
        int type = data.consumeInt(0, 6);
        switch (type) {
            case 0: return null;
            case 1: return data.consumeLong();
            case 2: return data.consumeInt();
            case 3: return data.consumeDouble();
            case 4: return data.consumeAsciiString(10);
            case 5: return java.math.BigDecimal.valueOf(data.consumeDouble());
            default: return new Object();
        }
    }
}