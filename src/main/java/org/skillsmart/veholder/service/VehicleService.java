package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    public Vehicle getVehicleById(Long id) {
        return repo.getReferenceById(id);
    }
}
