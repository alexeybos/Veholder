package org.skillsmart.veholder.controller;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EnterpriseRestControllerFuzzTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext context;

    // Мокаем KafkaTemplate, чтобы не отправлять реальные сообщения во время теста
    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @FuzzTest
    public void fuzzEnterpriseEndpoints(FuzzedDataProvider data) {
        // 1. Случайно выбираем эндпоинт из всего REST API
        String endpointTemplate = data.pickValue(Arrays.asList(
                "/api/enterprises",
                "/api/enterprises/{id}",
                "/api/enterprises/{id}/export",
                "/api/enterprises/{id}/drivers",
                "/api/enterprises/{id}/vehicles",
                "/api/enterprises/{id}/dashboard",
                "/api/enterprises/{id}/trips"
        ));

        // 2. Выбираем случайный HTTP-метод из поддерживаемых
        HttpMethod method = data.pickValue(Arrays.asList(
                HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE
        ));

        // 3. Генерируем случайные данные для подстановки в пути и параметры
        long id = data.consumeLong(1, 10000);
        String url = getBaseUrl() + endpointTemplate.replace("{id}", String.valueOf(id));

        // 4. Генерируем Query Parameters (для /export и /vehicles)
        if (url.contains("/export")) {
            String format = data.pickValue(Arrays.asList("json", "csv", "xml", "pdf"));
            url += "?format=" + format;
        }
        if (url.contains("/vehicles")) {
            int page = data.consumeInt(0, 5);
            int size = data.consumeInt(1, 20);
            String sort = data.pickValue(Arrays.asList("id", "model", "year", "mileage"));
            url += "?page=" + page + "&size=" + size + "&sort=" + sort;
        }

        // 5. Генерируем случайное JSON тело для POST и PUT запросов
        String requestBody = null;
        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            requestBody = generateRandomEnterprisePayload(data);
        }

        // 6. Отправляем запрос и проверяем на ошибки сервера (5xx)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, method, entity, String.class
            );

            // Тест считается проваленным, только если сервер вернул 500 ошибку.
            if (response.getStatusCode().is5xxServerError()) {
                Assertions.fail("Unexpected 5xx error: " + response.getStatusCode() +
                        " for " + method + " " + url + " with body: " + requestBody);
            }
        } catch (Exception e) {
            // Ловим любые другие исключения при выполнении запроса
            System.err.println("=== EXCEPTION CAUGHT ===");
            e.printStackTrace();
            Assertions.fail(e.toString());
            //Assertions.fail("Exception during request to " + method + " " + url + ": " + e.getMessage());
        }
    }

    private String generateRandomEnterprisePayload(FuzzedDataProvider data) {
        // Генерируем случайный JSON объект для создания/обновления предприятия
        Map<String, Object> payload = new HashMap<>();

        // Случайно выбираем, какие поля включить
        if (data.consumeBoolean()) {
            payload.put("name", data.consumeString(20));
        }
        if (data.consumeBoolean()) {
            payload.put("city", data.consumeString(15));
        }
        if (data.consumeBoolean()) {
            payload.put("address", data.consumeString(30));
        }
        if (data.consumeBoolean()) {
            payload.put("directorName", data.consumeString(25));
        }
        if (data.consumeBoolean()) {
            payload.put("phone", data.consumeString(15));
        }

        // Преобразуем в JSON строку (можно использовать Jackson или обычную конкатенацию)
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            // Если не получилось, возвращаем пустой объект
            return "{}";
        }
    }

    // Дополнительный тест с мок-авторизацией
    @FuzzTest(maxDuration = "3m")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    public void fuzzEnterpriseEndpointsWithAuth(FuzzedDataProvider data) {
        // Аналогичный тест, но с авторизацией под пользователем "manager"
        fuzzEnterpriseEndpoints(data);
    }
}