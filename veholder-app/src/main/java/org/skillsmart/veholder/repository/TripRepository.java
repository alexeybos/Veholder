package org.skillsmart.veholder.repository;

import com.vladmihalcea.hibernate.type.range.Range;
import org.skillsmart.veholder.entity.Trip;
import org.skillsmart.veholder.entity.dto.TripDTO;
import org.skillsmart.veholder.entity.dto.TripDatesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long>, JpaSpecificationExecutor<Trip> {

    /*@Query("SELECT new org.skillsmart.veholder.entity.dto.TripDTO(t.id, t.vehicleId, t.recordedAt) " +
            "FROM Trip t " +
            "WHERE t.vehicleId = :vehicleId " +
            "AND lower(t.recordedAt) >= :startDate " +
            "AND upper(t.recordedAt) <= :endDate")
    List<TripDTO> findTripsBetweenDates(
            @Param("vId") Long vId,
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate);*/

    @Query(value = "SELECT id, vehicle_id, lower(time_interval), upper(time_interval) FROM trips WHERE vehicle_id = :vehicleId AND recorded_at && tstzrange(:startDate, :endDate)",
            nativeQuery = true)
    List<TripDatesDTO> findTripsNative(@Param("vehicleId") Long vehicleId,
                                       @Param("startDate") ZonedDateTime startDate,
                                       @Param("endDate") ZonedDateTime endDate);

    /*@Query("SELECT new org.skillsmart.veholder.entity.dto.TripDTO(t.id, t.vehicleId, t.recordedAt) " +
            "FROM Trip t " +
            "WHERE t.vehicleId = :vId " +
            "AND lower(t.recordedAt) >= lower(:searchRange) " +
            "AND upper(t.recordedAt) <= upper(:searchRange)")
    List<TripDTO> findTripsInRange(
            @Param("vId") Long vId,
            @Param("searchRange") Range<ZonedDateTime> searchRange);*/
}
