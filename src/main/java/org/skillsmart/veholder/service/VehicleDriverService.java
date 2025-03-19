package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.VehicleDriver;
import org.skillsmart.veholder.entity.dto.VehicleDriverDto;
import org.skillsmart.veholder.repository.VehicleDriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleDriverService {

    @Autowired
    VehicleDriverRepository repo;

    public boolean isDriverActiveOnOneVehicle(Long id) {
        return true;
    }

    public List<VehicleDriverDto> getVehicleDriverRelations() {
        return repo.findAll().stream()
                .map(relation -> new VehicleDriverDto(relation.getId(), relation.getVehicle().getId(),
                        relation.getDriver().getId(), relation.isActive()))
                .toList();
    }

    public List<VehicleDriver> getFullVehicleDriverRelations() {
        return repo.findAll();
    }

    public VehicleDriver getRelationById(Long id) {
        return repo.getReferenceById(id);
    }

    public void save(VehicleDriver relation) {
        repo.save(relation);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
