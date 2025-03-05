package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandService {
    @Autowired
    BrandRepository repo;

    public void save(Brand brand) {
        repo.save(brand);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Brand> getList(Sort sortBy) {
        return repo.findAll(sortBy);
    }

    public Brand getBrandById(Long id) {
        //repo.getReferenceById();
        return repo.getReferenceById(id);
    }
}
