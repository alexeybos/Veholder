package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Driver;
import org.skillsmart.veholder.entity.dto.DriverDto;
import org.skillsmart.veholder.entity.dto.DriverNoDateDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query(
            value = "select d.id, d.name, d.salary, d.enterprise_id, d.vehicle_id, d.is_active from drivers d\n" +
                    "where d.enterprise_id in (select em.enterprises_id from enterprises_managers em join " +
                    "users u on u.id = em.managers_id and u.username = :username)",
            nativeQuery = true
    )
    List<DriverNoDateDto> getDriversDTOByManager(String username);
}
