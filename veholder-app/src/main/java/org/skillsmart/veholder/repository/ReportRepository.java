package org.skillsmart.veholder.repository;

import org.skillsmart.veholder.entity.Period;
import org.skillsmart.veholder.entity.report.MileageReport;
import org.skillsmart.veholder.entity.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAll();

    @Query("SELECT r FROM MileageReport r WHERE r.vehicle.id = :vehicleId")
    List<MileageReport> findMileageReportsByVehicle(@Param("vehicleId") Long vehicleId);

    // Другие специализированные запросы
    @Query(
            value = """
            WITH ordered_tracks AS (
                SELECT vehicle_id, point, recorded_at,
                    LAG(point, 1) OVER (PARTITION BY vehicle_id ORDER BY recorded_at) AS prev_point
                FROM vehicle_tracks
                WHERE recorded_at BETWEEN :startDate AND :endDate
                AND vehicle_id = :vehicle_id
            ),
            distances AS (
                SELECT vehicle_id,
                    ST_DistanceSphere(point, prev_point) AS distance_meters,
                    recorded_at
                FROM ordered_tracks
                WHERE prev_point IS NOT NULL
            )
           SELECT
                CASE
                    WHEN :period = 'DAY' THEN TO_CHAR(recorded_at, 'DD.MM.YYYY')
                    WHEN :period = 'MONTH' THEN TO_CHAR(recorded_at, 'MM.YYYY')
                    WHEN :period = 'YEAR' THEN TO_CHAR(recorded_at, 'YYYY')
                END AS period_name,
                SUM(distance_meters) AS period_distance
            FROM distances
            GROUP BY vehicle_id, period_name
            ORDER BY vehicle_id, period_name
           """,
            nativeQuery = true
    )
    List<Object[]> findVehicleMileageByPeriod(
            @Param("vehicle_id") Long vehicleId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            //@Param("periodGroup") String periodGroup, // 'day', 'week', 'month', 'year'
            @Param("period") String period
    );

    default List<Object[]> findMileage(Long vehicleId,
                                       LocalDateTime startDate,
                                       LocalDateTime endDate,
                                       Period period) {
        String extractExpr = switch (period) {
            case DAY -> "DAY";
            case WEEK -> "WEEK";
            case MONTH -> "MONTH";
            case QUARTER -> "QUARTER";
            case YEAR -> "YEAR";
        };

        return findVehicleMileageByPeriod(vehicleId, startDate, endDate, extractExpr);
    }

    @Query(
            value = """
            SELECT
              day,
              ROUND(SUM(EXTRACT(EPOCH FROM
                  LEAST(upper(time_interval), day + INTERVAL '1 day') -
                  GREATEST(lower(time_interval), day)
              ))/900) AS driving_hours,
              ROUND((ROUND(SUM(EXTRACT(EPOCH FROM
                  LEAST(upper(time_interval), day + INTERVAL '1 day') -
                  GREATEST(lower(time_interval), day)
              ))/900) * (d.salary/24/8))::numeric, 2) AS salary
          FROM
              trips t,
              drivers d,
              GENERATE_SERIES(
                  DATE_TRUNC('day', lower(time_interval)),
                  DATE_TRUNC('day', upper(time_interval)),
                  INTERVAL '1 day'
              ) AS days(day)
          WHERE
              t.vehicle_id = :vehicle_id
              and d.vehicle_id=t.vehicle_id and d.is_active = true
              AND time_interval IS NOT NULL
          GROUP BY
              day, d.salary
          ORDER BY
              day
           """,
            nativeQuery = true
    )
    List<Object[]> findDriverHoursByDay(
            @Param("vehicle_id") Long vehicleId
    );

    @Query(
            value = """
             SELECT
                 DATE_TRUNC('month', lower(time_interval)) AS month,
                 ROUND(EXTRACT(EPOCH FROM SUM(upper(time_interval) - lower(time_interval)))/900) AS total_hours,
                 ROUND((ROUND(EXTRACT(EPOCH FROM SUM(upper(time_interval) - lower(time_interval)))/900) * (d.salary/24/8))::numeric, 2) as salary
             FROM
                 trips t
             inner join drivers d on d.vehicle_id=t.vehicle_id and d.is_active = true
             WHERE
                 t.vehicle_id = :vehicle_id
             AND time_interval IS NOT NULL
             GROUP BY
                 DATE_TRUNC('month', lower(time_interval)), d.salary
             ORDER BY
                 month
           """,
            nativeQuery = true
    )
    List<Object[]> findDriverHoursByMonth(
            @Param("vehicle_id") Long vehicleId
    );

    default List<Object[]> findDriverHours(Long vehicleId,
                                       LocalDateTime startDate,
                                       LocalDateTime endDate,
                                       Period period) {
        String extractExpr = switch (period) {
            case DAY -> "DAY";
            case WEEK -> "WEEK";
            case MONTH -> "MONTH";
            case QUARTER -> "QUARTER";
            case YEAR -> "YEAR";
        };
        if (extractExpr.equals("DAY")) return findDriverHoursByDay(vehicleId);
        return findDriverHoursByMonth(vehicleId);
    }


}
