package org.skillsmart.veholder;

import org.junit.jupiter.api.Test;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.dto.BrandDto;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BrandServiceIntegrationTest {

    @Autowired
    private BrandRepository repo;
    @Autowired
    private BrandService service;

    @Test
    void testUpdateBrand_updatesInDB() {
        //repo.deleteAll();
        Brand brand = new Brand();
        brand.setName("New Brand");
        brand.setNumberOfSeats(5);
        brand.setLoadCapacity(1000);
        brand.setTank(50);
        brand.setType("bus");
        Brand createdBrand = repo.saveAndFlush(brand);

        BrandDto brandDto = new BrandDto(createdBrand.getId(), "New Brand", "null", 2000, 100, 5);
        service.updateBrand(createdBrand.getId(), brandDto);

        Brand resultBrand = repo.findByName("New Brand");
        assertEquals(2000, resultBrand.getLoadCapacity());
        assertEquals(100, resultBrand.getTank());
        assertEquals(5, resultBrand.getNumberOfSeats());
        assertEquals("New Brand", resultBrand.getName());
    }
}
