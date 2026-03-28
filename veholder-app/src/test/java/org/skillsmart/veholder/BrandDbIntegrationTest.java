package org.skillsmart.veholder;

import org.junit.jupiter.api.Test;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.dto.BrandDto;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(BrandService.class)
public class BrandDbIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");
    }
    @Autowired
    private BrandRepository repo;
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private BrandService service;
    @Test
    void testCreateAndUpdateBrand() {
        Brand testBrand = new Brand();
        testBrand.setName("Test Brand");
        testBrand.setNumberOfSeats(5);
        testBrand.setLoadCapacity(1000);
        testBrand.setTank(50);
        testBrand.setType("bus");
        Brand createdBrand = repo.saveAndFlush(testBrand);
        assertNotNull(createdBrand.getId());
        assertEquals(1000, createdBrand.getLoadCapacity());
        assertEquals(50, createdBrand.getTank());
        BrandDto brandForUpdate = new BrandDto(createdBrand.getId(), createdBrand.getName(),
                createdBrand.getType(), createdBrand.getLoadCapacity(), createdBrand.getTank(), 10);
        service.updateBrand(createdBrand.getId(), brandForUpdate);
        Brand resultBrand = repo.findByName("Test Brand");
        assertNotNull(resultBrand);
        assertEquals("Test Brand", resultBrand.getName());
        assertEquals(10, resultBrand.getNumberOfSeats());
    }
}
