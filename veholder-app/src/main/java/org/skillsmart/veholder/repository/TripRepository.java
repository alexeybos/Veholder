package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
