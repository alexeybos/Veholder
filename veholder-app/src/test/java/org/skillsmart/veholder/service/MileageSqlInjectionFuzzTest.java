package org.skillsmart.veholder.service;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import jakarta.persistence.EntityNotFoundException;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.fuzz.PostgresInitializer;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.service.MileageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("fuzz")
@ContextConfiguration(initializers = PostgresInitializer.class)
class MileageSqlInjectionFuzzTest {

    @Autowired private MileageService service;
    @Autowired private EnterpriseRepository enterpriseRepository;
    private static Long seededEnterpriseId;
    private void seedIfNeeded() {
        if (seededEnterpriseId == null) {
            Enterprise e = new Enterprise();
            e.setName("FuzzCo");
            seededEnterpriseId = enterpriseRepository.save(e).getId();
        }
    }
    @FuzzTest(maxDuration = "10m")
    void fuzzEnterpriseMileageSql(FuzzedDataProvider data) {
        seedIfNeeded();
        // Половину времени — валидный id, половину — произвольный
        long enterpriseId = data.consumeBoolean() ? seededEnterpriseId : data.consumeLong();
        String dateString = buildPayload(data);
        try {
            service.getEnterpriseMileage(enterpriseId, dateString);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            // ожидаемо
        } catch (org.springframework.dao.DataAccessException e) {
            // SQL-ошибки БД — тоже ожидаемы при кривом вводе
        }
        // Если сработал SqlInjectionSanitizer — тест упадёт с FuzzerSecurityIssueHigh
    }
    private String buildPayload(FuzzedDataProvider data) {
        int mode = data.consumeInt(0, 4);
        switch (mode) {
            case 0: // валидный формат
                return data.consumeInt(1, 31) + "." + data.consumeInt(1, 12) + "." + data.consumeInt(1900, 2100);
            case 1: // классические injection-паттерны
                String[] attacks = {
                        "1' OR '1'='1",
                        "'; DROP TABLE vehicle_track;--",
                        "1 UNION SELECT null,null,null,null",
                        "1/**/OR/**/1=1",
                        "1';--"
                };
                return data.pickValue(attacks);
            case 2: // мутация атаки + валидный хвост
                return data.consumeAsciiString(15) + ".01.2024";
            case 3: // произвольный мусор
                return data.consumeAsciiString(40);
            default:
                return null;
        }
    }
}