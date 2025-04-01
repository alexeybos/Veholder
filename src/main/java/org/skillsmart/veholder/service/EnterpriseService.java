package org.skillsmart.veholder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.dto.EnterprisesDriversDto;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnterpriseService {

    @Autowired
    private EnterpriseRepository repo;
    @Autowired
    private final ObjectMapper objectMapper;

    public EnterpriseService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Enterprise> getEnterprises() {
        Sort sort = Sort.by("id").ascending();
        return repo.findAll(sort);
    }

    public List<Enterprise> getEnterprisesByManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return repo.getEnterprisesByManager(username);
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

    public EnterprisesDriversDto getDriversByEnterpriseId(Long id) {
        List<EnterprisesDriversDto> result = repo.getDriversByEnterprise(id);
        return result.getFirst();
    }

    public JsonNode getDriversByEnterpriseIdJson(Long id) {
        String result = repo.getDriversByEnterpriseJson(id);
        try {
            return objectMapper.readTree(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    public JsonNode getFullEnterpriseInfoById(Long id) {
        try {
            String result = repo.getFullEnterpriseInfoById(id);
            return objectMapper.readTree(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}
