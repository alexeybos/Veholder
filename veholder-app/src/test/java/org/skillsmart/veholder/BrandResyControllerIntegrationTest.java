package org.skillsmart.veholder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skillsmart.veholder.controller.BrandRestController;
import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.service.BrandService;
import org.skillsmart.veholder.service.JdbcBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(
        value = BrandRestController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import(BrandService.class)
public class BrandResyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BrandRepository repo;

    @Test
    void testGetBrandById() throws Exception {
        Brand brand = new Brand();
        brand.setId(10L);
        brand.setName("New Brand");
        brand.setNumberOfSeats(5);
        brand.setLoadCapacity(1000);
        brand.setTank(50);
        brand.setType("bus");

        when(repo.getReferenceById(10L)).thenReturn(brand);

        mockMvc.perform(get("/api/brands/10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("New Brand")));
    }

}
