package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.dto.EnterprisesDriversDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    @Query(
            value = "select \n" +
                    "e.id,\n" +
                    "e.name, \n" +
                    "json_agg(\n" +
                    "\tjson_build_object(\n" +
                    "\t\t'driverId', d.id,\n" +
                    "\t\t'name', d.\"name\",\n" +
                    "\t\t'vvehicleId', d.vehicle_id,\n" +
                    "\t\t'isActive', d.is_active\n" +
                    "\t)\n" +
                    ") as drivers\n" +
                    "from enterprises e \n" +
                    "left join drivers d on d.enterprise_id = e.id\n" +
                    "where e.id = :enterpriseId\n" +
                    "group by e.id, e.name",
            nativeQuery = true
    )
    List<EnterprisesDriversDto> getDriversByEnterprise(@Param("enterpriseId") Long id);

    @Query(
            value = "select \n" +
                    "json_build_object(\n" +
                    "\t'id', e.id,\n" +
                    "\t'name', e.name,\n" +
                    "\t'drivers', json_agg(\n" +
                    "\t\tjson_build_object(\n" +
                    "\t\t\t'driverId', d.id,\n" +
                    "\t\t\t'name', d.\"name\",\n" +
                    "\t\t\t'vvehicleId', d.vehicle_id,\n" +
                    "\t\t\t'isActive', d.is_active\n" +
                    "\t\t)\n" +
                    "\t)\n" +
                    ")\n" +
                    "from enterprises e \n" +
                    "left join drivers d on d.enterprise_id = e.id\n" +
                    "where e.id = :enterpriseId\n" +
                    "group by e.id, e.name",
            nativeQuery = true
    )
    String getDriversByEnterpriseJson(@Param("enterpriseId") Long id);

    @Query(
            value = "select \n" +
                    "json_build_object(\n" +
                    "\t'id', e.id,\n" +
                    "\t'name', e.name,\n" +
                    "\t'city', e.city, \n" +
                    "\t'director_name', e.director_name,\n" +
                    "\t'drivers', (\n" +
                    "\t\tselect json_agg(d.id) from drivers d where d.enterprise_id = e.id\n" +
                    "\t\t),\n" +
                    "\t'vehicles', (\n" +
                    "\t\tselect json_agg(v.id) from vehicle v where v.enterprise_id = e.id\n" +
                    "\t)\n" +
                    "\t)\n" +
                    "from enterprises e \n" +
                    "where e.id = 1\n" +
                    "group by e.id, e.name, e.city, e.director_name",
            nativeQuery = true
    )
    String getFullEnterpriseInfoById(@Param("enterpriseId") Long id);

    @Query(
            value = "select e.id, e.name, e.city, e.director_name from enterprises e \n" +
                    "join enterprises_managers em on em.enterprises_id = e.id\n" +
                    "join users u on u.id = em.managers_id and u.username = :username",
            nativeQuery = true
    )
    List<Enterprise> getEnterprisesByManager(String username);
}
