package org.skillsmart.veholder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Manager;
import org.skillsmart.veholder.entity.dto.EnterpriseDto;
import org.skillsmart.veholder.entity.dto.EnterprisesDriversDto;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class EnterpriseService {

    @Autowired
    private EnterpriseRepository repo;
    @Autowired
    private ManagerRepository managerRepo;
    @Autowired
    private final ObjectMapper objectMapper;

    public EnterpriseService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Enterprise> getEnterprises() {
        Sort sort = Sort.by("id").ascending();
        return repo.findAll(sort);
    }

    public List<EnterpriseDto> getEnterprisesByManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getEnterprisesByManager(authentication.getName());
    }

    public List<EnterpriseDto> getEnterprisesByManager(String username) {
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

    public boolean checkEnterpriseByManager(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return repo.checkEnterpriseByManager(id, username) > 0;
    }

    public Long createEnterprise(Enterprise enterprise) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Manager> manager = managerRepo.findByUsername(username);
        if (manager.isEmpty()) {
            throw new AccessDeniedException("Создание предприятия разрешено только менеджеру. Пользователь не является менеджером!");
        }
        enterprise.getManagers().add(manager.get());
        Enterprise created = repo.save(enterprise);
        return created.getId();
    }

    public void updateEnterprise(Long id, Map<String, Object> values) {
        if (!checkEnterpriseByManager(id)) {
            throw new AccessDeniedException("Можно редактировать только свое предприятие!");
        }
        Enterprise enterprise = repo.getReferenceById(id);
        enterprise.setCity(values.getOrDefault("city", enterprise.getCity()).toString());
        enterprise.setName(values.getOrDefault("name", enterprise.getName()).toString());
        enterprise.setDirectorName(values.getOrDefault("directorName", enterprise.getDirectorName()).toString());
        repo.save(enterprise);
        repo.flush();
    }

    public void deleteEnterprise(Long id) {
        if (!checkEnterpriseByManager(id)) {
            throw new AccessDeniedException("Можно удалить только свое предприятие!");
        }
        //repo.deleteById(id);
        repo.deleteEnterpriseManagersLink(id);
        repo.deleteEnterprise(id);
        repo.flush();
    }
}
