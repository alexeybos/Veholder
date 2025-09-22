package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.VehicleTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
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

    @Query(value = """
        SELECT round(COALESCE(SUM(ST_Distance(
            ST_Transform(vt1.point, 3857),
            ST_Transform(vt2.point, 3857)
        ))::numeric, 0), 2)
        FROM vehicle_tracks vt1
        JOIN vehicle_tracks vt2 ON vt1.vehicle_id = vt2.vehicle_id 
            AND vt2.recorded_at = (
                SELECT MIN(vt3.recorded_at) 
                FROM vehicle_tracks vt3 
                WHERE vt3.vehicle_id = vt1.vehicle_id 
                AND vt3.recorded_at > vt1.recorded_at
                AND DATE(vt3.recorded_at AT TIME ZONE 'UTC') = DATE(:date)
            )
        WHERE vt1.vehicle_id = :vehicleId 
        AND DATE(vt1.recorded_at AT TIME ZONE 'UTC') = DATE(:date)
        """, nativeQuery = true)
    Double findDailyMileageByVehicleId(@Param("vehicleId") Long vehicleId,
                                       @Param("date") LocalDate date);

    @Query(value = """
        SELECT round(COALESCE(SUM(ST_Distance(
            ST_Transform(vt1.point, 3857),
            ST_Transform(vt2.point, 3857)
        ))::numeric, 0), 2)
        FROM vehicle_tracks vt1
        JOIN vehicle_tracks vt2 ON vt1.vehicle_id = vt2.vehicle_id 
            AND vt2.recorded_at = (
                SELECT MIN(vt3.recorded_at) 
                FROM vehicle_tracks vt3 
                WHERE vt3.vehicle_id = vt1.vehicle_id 
                AND vt3.recorded_at > vt1.recorded_at
                AND EXTRACT(YEAR FROM vt3.recorded_at AT TIME ZONE 'UTC') = EXTRACT(YEAR FROM CAST(:monthDate  AS timestamp))
                AND EXTRACT(MONTH FROM vt3.recorded_at AT TIME ZONE 'UTC') = EXTRACT(MONTH FROM CAST(:monthDate  AS timestamp))
            )
        WHERE vt1.vehicle_id = :vehicleId 
        AND EXTRACT(YEAR FROM vt1.recorded_at AT TIME ZONE 'UTC') = EXTRACT(YEAR FROM CAST(:monthDate  AS timestamp))
        AND EXTRACT(MONTH FROM vt1.recorded_at AT TIME ZONE 'UTC') = EXTRACT(MONTH FROM CAST(:monthDate  AS timestamp))
        """, nativeQuery = true)
    Double findMonthlyMileageByVehicleId(@Param("vehicleId") Long vehicleId,
                                         @Param("monthDate") LocalDate monthDate);

    @Query(value = """
        SELECT v.id as vehicleId, v.registration_number as registrationNumber, d.name, 
        round(COALESCE(SUM(ST_Distance(
            ST_Transform(vt1.point, 3857),
            ST_Transform(vt2.point, 3857)
        ))::numeric, 0), 2) as mileage
        FROM vehicle v
        inner join drivers d on v.id = d.vehicle_id and d.is_active = true
        LEFT JOIN vehicle_tracks vt1 ON v.id = vt1.vehicle_id
        LEFT JOIN vehicle_tracks vt2 ON vt1.vehicle_id = vt2.vehicle_id
            AND vt2.recorded_at = (
                SELECT MIN(vt3.recorded_at) 
                FROM vehicle_tracks vt3 
                WHERE vt3.vehicle_id = vt1.vehicle_id 
                AND vt3.recorded_at > vt1.recorded_at
                AND DATE(vt3.recorded_at AT TIME ZONE 'UTC') = DATE(:date)
            )
        WHERE v.enterprise_id = :enterpriseId
        AND DATE(vt1.recorded_at AT TIME ZONE 'UTC') = DATE(:date)
        GROUP BY v.id, v.registration_number, d.name
        """, nativeQuery = true)
    List<Object[]> findDailyMileageByEnterpriseId(@Param("enterpriseId") Long enterpriseId,
                                                  @Param("date") LocalDate date);

    @Query(value = """
        SELECT v.id as vehicleId, v.registration_number as registrationNumber, d.name,
        round(COALESCE(SUM(ST_Distance(
            ST_Transform(vt1.point, 3857),
            ST_Transform(vt2.point, 3857)
        ))::numeric, 0), 2) as mileage
        FROM vehicle v
        inner join drivers d on v.id = d.vehicle_id and d.is_active = true
        LEFT JOIN vehicle_tracks vt1 ON v.id = vt1.vehicle_id
        LEFT JOIN vehicle_tracks vt2 ON vt1.vehicle_id = vt2.vehicle_id
            AND vt2.recorded_at = (
                SELECT MIN(vt3.recorded_at)
                FROM vehicle_tracks vt3 
                WHERE vt3.vehicle_id = vt1.vehicle_id 
                AND vt3.recorded_at > vt1.recorded_at
                AND EXTRACT(YEAR FROM vt3.recorded_at AT TIME ZONE 'UTC') = EXTRACT(YEAR FROM CAST(:monthDate AS timestamp))
                AND EXTRACT(MONTH FROM vt3.recorded_at AT TIME ZONE 'UTC') = EXTRACT(MONTH FROM CAST(:monthDate AS timestamp))
            )
        WHERE v.enterprise_id = :enterpriseId
        AND EXTRACT(YEAR FROM vt1.recorded_at AT TIME ZONE 'UTC') = EXTRACT(YEAR FROM CAST(:monthDate AS timestamp))
        AND EXTRACT(MONTH FROM vt1.recorded_at AT TIME ZONE 'UTC') = EXTRACT(MONTH FROM CAST(:monthDate AS timestamp))
        GROUP BY v.id, v.registration_number, d.name
        """, nativeQuery = true)
    List<Object[]> findMonthlyMileageByEnterpriseId(@Param("enterpriseId") Long enterpriseId,
                                                    @Param("monthDate") LocalDate monthDate);
}
