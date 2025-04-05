package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Manager;
import org.skillsmart.veholder.entity.dto.ManagerDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByUsername(String username);

    @Query(
            value = "select u.id, u.username, u.password, u.full_name, json_agg(em.enterprises_id) as enterprise_info from users u\n" +
                    "join enterprises_managers em on em.managers_id = u.id\n" +
                    "group by u.id, u.username, u.full_name",
            nativeQuery = true
    )
    List<ManagerDTO> getManagersList();
}
