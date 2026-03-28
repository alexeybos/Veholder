package org.skillsmart.veholder;

import org.junit.jupiter.api.Test;
import org.skillsmart.veholder.controller.JwtAuthController.LoginRequest;
import org.skillsmart.veholder.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BrandFullControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testFindBrandByName() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getTokenForTest());
        ResponseEntity<Brand> response = restTemplate.exchange(
                "/api/brands/find?name=Икарус",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Brand.class
        );
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
        Brand brand = response.getBody();
        assertNotNull(brand);
        assertEquals(6, brand.getId());
        assertEquals(4500, brand.getLoadCapacity());
        assertEquals(150, brand.getNumberOfSeats());
        assertEquals("автобус", brand.getType());
        assertEquals(300, brand.getTank());
    }

    private String getTokenForTest() {
        LoginRequest loginRequest = new LoginRequest("man1", "1111");
        ResponseEntity<Map<String, String>> authResponse = restTemplate.exchange(
                "/api/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<Map<String, String>>() {}
        );
        if (authResponse.getStatusCode() == HttpStatus.OK && authResponse.getBody() != null) {
            return authResponse.getBody().get("token"); // "Bearer eyJhbGciOiJ..."
        }
        return null;
    }

}
