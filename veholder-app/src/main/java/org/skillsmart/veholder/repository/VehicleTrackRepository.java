package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.VehicleTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

public interface VehicleTrackRepository extends JpaRepository<VehicleTrack, Long>, JpaSpecificationExecutor<VehicleTrack> {

    @Query("SELECT vt FROM VehicleTrack vt " +
            "WHERE vt.vehicleId = :vehicleId " +
            "AND vt.recordedAt BETWEEN :start AND :end " +
            "ORDER BY vt.recordedAt")
    List<VehicleTrack> findTracksByVehicleAndTimeRange(
            @Param("vehicleId") Long vehicleId,
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end);

    @Query("SELECT COUNT(t) > 0 FROM VehicleTrack t WHERE t.vehicleId = :vehicleId " +
            "AND t.recordedAt BETWEEN :startDate AND :endDate")
    boolean existsByVehicleAndDateRange(
            @Param("vehicleId") Long vehicleId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query("SELECT MIN(t.recordedAt), MAX(t.recordedAt) FROM VehicleTrack t WHERE t.vehicleId = :vehicleId")
    List<Object[]> findDateRangeByVehicle(@Param("vehicleId") Long vehicleId);
}
