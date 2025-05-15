package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleProjection;
import org.skillsmart.veholder.entity.dto.VehicleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface VehiclePagingRepository extends PagingAndSortingRepository<Vehicle, Long> {
    List<VehicleProjection> findAllProjectedBy(Sort sortBy);
    VehicleProjection getReferenceProjectedById(Long id);

    @Query(
            value = "select v.id, v.color, v.brand_id, v.enterprise_id, v.in_order, v.mileage, v.price, v.registration_number, v.year_of_production \n" +
                    "from vehicle v where v.enterprise_id in (select em.enterprises_id from enterprises_managers em join users u on u.id = em.managers_id " +
                    "and u.username = :manager)",
            nativeQuery = true
    )
    List<VehicleProjection> getVehiclesByManager(String manager);

    @Query(
            value = "select v.id, v.color, v.brand_id, v.enterprise_id, v.in_order, v.mileage, v.price, v.registration_number, v.year_of_production \n" +
                    "from vehicle v where v.enterprise_id in (select em.enterprises_id from enterprises_managers em join users u on u.id = em.managers_id " +
                    "and u.username = :manager)",
            nativeQuery = true
    )
    Page<VehicleProjection> getVehiclesByManagerPaging(Pageable pageable, String manager);

    @Query(
            value = "select v.id, v.year_of_production, v.price, v.color, v.mileage, v.registration_number, v.in_order, v.brand_id, v.enterprise_id, " +
                    "b.name as brand_name, v.purchase_utc as purchase_utc, v.purchase_utc AT TIME ZONE coalesce(e.timezone, 'UTC') as purchase_date \n" +
                    "from vehicle v join brand b on b.id = v.brand_id " +
                    "inner join enterprises e on e.id = v.enterprise_id " +
                    "where v.enterprise_id in (select em.enterprises_id from enterprises_managers em join users u on u.id = em.managers_id " +
                    "and u.username = :manager where em.enterprises_id = :enterpriseId)",
            nativeQuery = true
    )
    Page<VehicleDTO> getVehiclesByEnterprise(Pageable pageable, Long enterpriseId, String manager);

}
