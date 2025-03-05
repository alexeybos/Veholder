package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<VehicleProjection> findAllProjectedBy(Sort sortBy);
    VehicleProjection getReferenceProjectedById(Long id);
}
