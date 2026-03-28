package org.skillsmart.veholder;

import org.junit.jupiter.api.Test;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "integration_data/test_brand_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BrandRepoIntegrationTest {

    @Autowired
    private BrandRepository repo;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void testFindByName_existsBrandInDB() {
        Brand brand = repo.findByName("car");
        assertEquals(1000, brand.getId());
        assertEquals("car", brand.getName());
    }

}
