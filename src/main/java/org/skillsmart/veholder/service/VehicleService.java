package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleProjection;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleService {

    @Autowired
    VehicleRepository repo;

    public void save(Vehicle vehicle) {
        repo.save(vehicle);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Vehicle> getList(Sort sortBy) {
        return repo.findAll(sortBy);

    }

    public List<VehicleProjection> getOnlyVehiclesList(Sort sortBy) {
        return repo.findAllProjectedBy(sortBy);
    }

    public List<VehicleProjection> getOnlyVehiclesListForManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return repo.getVehiclesByManager(username);
    }

    public VehicleProjection getVehicleProjectedById(Long id) {
        return repo.getReferenceProjectedById(id);
    }

    public Vehicle getVehicleById(Long id) {
        return repo.getReferenceById(id);
    }
}
