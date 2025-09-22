package org.skillsmart.veholder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EnterpriseIOService {

    @Autowired
    private EnterpriseRepository repo;
    @Autowired
    private ManagerRepository managerRepo;
    @Autowired
    private final ObjectMapper objectMapper;

    public EnterpriseIOService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


}
