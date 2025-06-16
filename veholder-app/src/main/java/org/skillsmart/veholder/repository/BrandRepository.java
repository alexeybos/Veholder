package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Page<Brand> findAllByIdIn(Collection<Long> ids, Pageable pageable);
}
