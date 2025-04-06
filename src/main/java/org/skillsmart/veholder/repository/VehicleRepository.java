package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<VehicleProjection> findAllProjectedBy(Sort sortBy);
    VehicleProjection getReferenceProjectedById(Long id);

    @Query(
            value = "select v.id, v.color, v.brand_id, v.enterprise_id, v.in_order, v.mileage, v.price, v.registration_number, v.year_of_production \n" +
                    "from vehicle v where v.enterprise_id in (select em.enterprises_id from enterprises_managers em join users u on u.id = em.managers_id " +
                    "and u.username = :manager)",
            nativeQuery = true
    )
    List<VehicleProjection> getVehiclesByManager(String manager);
}
