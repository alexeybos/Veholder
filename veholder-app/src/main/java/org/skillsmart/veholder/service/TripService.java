package org.skillsmart.veholder.service;

import com.vladmihalcea.hibernate.type.range.Range;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.skillsmart.veholder.entity.Trip;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleTrack;
import org.skillsmart.veholder.entity.dto.TripDatesDTO;
import org.skillsmart.veholder.entity.dto.TripDescriptionDTO;
import org.skillsmart.veholder.repository.TripRepository;
import org.skillsmart.veholder.utils.GeoJsonFeatureCollection;
import org.skillsmart.veholder.utils.YandexGeocoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.skillsmart.veholder.repository.spec.TripSpecifications.byVehicleId;
import static org.skillsmart.veholder.repository.spec.TripSpecifications.timeIntervalOverlaps;

@Slf4j
@Service
public class TripService {

    @Autowired
    private TripRepository repo;

    @Autowired
    private VehicleTrackService trackService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private TimezoneService timezoneService;

    public List<?> getTripsTracksByTimeRange(Long vehicleId, ZonedDateTime start, ZonedDateTime end, String format) {
        //List<TripDTO> trips = repo.findTripsBetweenDates(vehicleId, start, end);
        List<TripDatesDTO> trips = repo.findTripsNative(vehicleId, start, end);
        List result = new ArrayList<>();
        for (int i = 0; i < trips.size(); i++) {
            /*List<?> tracks = trackService.getVehicleTrack(vehicleId, trips.get(i).timeInterval().lower(),
                    trips.get(i).timeInterval().upper(), format);*/
            /*List<?> tracks = trackService.getVehicleTrack(vehicleId, trips.get(i).startInterval().atZone(ZoneOffset.UTC),
                    trips.get(i).endInterval().atZone(ZoneOffset.UTC), format);*/
            if ("geojson".equalsIgnoreCase(format)) {
                List<List<GeoJsonFeatureCollection>> tracks = (List<List<GeoJsonFeatureCollection>>) trackService.getVehicleTrack(vehicleId, trips.get(i).startInterval().atZone(ZoneOffset.UTC),
                        trips.get(i).endInterval().atZone(ZoneOffset.UTC), format);
                //result.addAll(tracks.getFirst());
                return tracks;
            } else {
                List<?> tracks = trackService.getVehicleTrack(vehicleId, trips.get(i).startInterval().atZone(ZoneOffset.UTC),
                        trips.get(i).endInterval().atZone(ZoneOffset.UTC), format);
                result.addAll(tracks);
            }
        }
        return result;
    }

    /**
     * Найти поездки, полностью входящие в интервал
     */
    public List<Trip> findTripsWithinInterval(Long vehicleId, ZonedDateTime start, ZonedDateTime end) {
        Specification<Trip> spec = Specification.where(byVehicleId(vehicleId))
            .and(timeIntervalOverlaps(start, end));

        return repo.findAll(spec);
    }

    /**
     * Полная информация по поездкам в интервале
     */
    //@Cacheable("tripInfo")
    public List<TripDescriptionDTO> getTripsInfo(Long vehicleId, ZonedDateTime start, ZonedDateTime end) throws Exception {
        //log.info("Getting trio info for vehicleId {} ({} - {})", vehicleId, start, end);
        List<Trip> trips = findTripsWithinInterval(vehicleId, start, end);
        List<TripDescriptionDTO> tripsInfo = new ArrayList<>();
        ZoneId enterpriseZone = timezoneService.getEnterpriseTimeZoneByVehicle(vehicleId);
        for (Trip trip : trips) {
            List<VehicleTrack> points = trackService.getStartAndEndPointByInterval(vehicleId,
                    trip.getTimeInterval().lower(), trip.getTimeInterval().upper());
            VehicleTrack first = points.getFirst();
            VehicleTrack last = points.getLast();
            tripsInfo.add(new TripDescriptionDTO(trip.getId(), trip.getVehicle().getId(),
                    trip.getTimeInterval().lower().withZoneSameInstant(enterpriseZone),
                    trip.getTimeInterval().upper().withZoneSameInstant(enterpriseZone),
                    timezoneService.getFormattedDateTimeInEnterpriseZone(trip.getTimeInterval().lower(), enterpriseZone),
                    timezoneService.getFormattedDateTimeInEnterpriseZone(trip.getTimeInterval().upper(), enterpriseZone),
                    first.getId(), last.getId(),
                    //        "Проверка скорости запроса без геокодера",
                    YandexGeocoder.getAddressDescByYandex(first.getPoint().getX(), first.getPoint().getY()),
                    "Проверка скорости запроса без геокодера") //YandexGeocoder.getAddressDescByYandex(last.getPoint().getX(), last.getPoint().getY()))
            );
        }
        return tripsInfo;
    }

    public void createTrip(Long vehicleId, ZonedDateTime start, ZonedDateTime end) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle == null) {
            throw new EntityNotFoundException("vehicleId " + vehicleId + " not found");
        }
        Range<ZonedDateTime> interval = Range.closed(start, end);
        Trip trip = new Trip();
        trip.setVehicle(vehicle);
        trip.setTimeInterval(interval);
        repo.save(trip);
    }

    //try (Session session = sessionFactory.openSession()) {
    //            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    //            CriteriaQuery<Trip> criteriaQuery = criteriaBuilder.createQuery(Trip.class);
    //            Root<Trip> tripRoot = criteriaQuery.from(Trip.class);
    //
    //            // Условие для временного интервала
    //            criteriaQuery.select(tripRoot)
    //                    .where(criteriaBuilder.and(
    //                            criteriaBuilder.lessThanOrEqualTo(tripRoot.get("timeInterval").get("upper"), endTime),
    //                            criteriaBuilder.greaterThanOrEqualTo(tripRoot.get("timeInterval").get("lower"), startTime)
    //                    ));
    //
    //            return session.createQuery(criteriaQuery).getResultList();
    //        }
}
