package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Driver;
import org.skillsmart.veholder.entity.dto.DriverNoDateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DriverPagingRepository extends PagingAndSortingRepository<Driver, Long> {

    @Query(
            value = "select d.id, d.name, d.salary, d.enterprise_id, d.vehicle_id, d.is_active from drivers d\n" +
                    "where d.enterprise_id in (select em.enterprises_id from enterprises_managers em join " +
                    "users u on u.id = em.managers_id and u.username = :username)",
            nativeQuery = true
    )
    Page<DriverNoDateDto> getDriversDTOByManager(Pageable pageable, String username);

    Page<Driver> findByEnterpriseId(Long enterpriseId, Pageable pageable);
}
