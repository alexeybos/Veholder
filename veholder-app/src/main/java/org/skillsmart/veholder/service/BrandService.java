package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.dto.BrandDto;
import org.skillsmart.veholder.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrandService {
    @Autowired
    BrandRepository repo;

    @Transactional
    public Brand updateBrand(Long id, BrandDto brandDetails) {
        Brand brand = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found with id: " + id));

        if (brandDetails.name() != null) {
            brand.setName(brandDetails.name());
        }
        if (brandDetails.type() != null) {
            brand.setType(brandDetails.type());
        }
        if (brandDetails.loadCapacity() != 0) {
            brand.setLoadCapacity(brandDetails.loadCapacity());
        }
        if (brandDetails.tank() != 0) {
            brand.setTank(brandDetails.tank());
        }
        if (brandDetails.numberOfSeats() != 0) {
            brand.setNumberOfSeats(brandDetails.numberOfSeats());
        }

        return repo.save(brand);
    }
}
