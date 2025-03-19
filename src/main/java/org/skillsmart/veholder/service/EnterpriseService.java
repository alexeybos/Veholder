package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnterpriseService {

    @Autowired
    private EnterpriseRepository repo;

    public List<Enterprise> getEnterprises() {
        Sort sort = Sort.by("id").ascending();
        return repo.findAll(sort);
    }

    public Enterprise getEnterpriseById(Long id) {
        return repo.getReferenceById(id);
    }

    public void save(Enterprise enterprise) {
        repo.save(enterprise);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
