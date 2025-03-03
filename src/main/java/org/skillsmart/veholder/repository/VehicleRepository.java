package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
