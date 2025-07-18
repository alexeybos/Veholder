package org.skillsmart.veholder.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleTrack;
import org.skillsmart.veholder.entity.dto.VehicleTrackDto;
import org.skillsmart.veholder.jaxb.Gpx;
import org.skillsmart.veholder.jaxb.Track;
import org.skillsmart.veholder.jaxb.TrackSegment;
import org.skillsmart.veholder.jaxb.Waypoint;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.skillsmart.veholder.repository.VehicleTrackRepository;
import org.skillsmart.veholder.utils.GeoJsonFeature;
import org.skillsmart.veholder.utils.GeoJsonFeatureCollection;
import org.skillsmart.veholder.utils.GeoJsonGeometry;
import org.skillsmart.veholder.utils.GeoJsonPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.skillsmart.veholder.repository.spec.TrackSpecifications.*;

@Service
public class VehicleTrackService {

    @Autowired
    private VehicleTrackRepository repo;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private TimezoneService timezoneService;
    @PersistenceContext
    private EntityManager entityManager;

    public List<?> getVehicleTrack(Long vehicleId, ZonedDateTime start, ZonedDateTime end, String format) {
        List<VehicleTrack> tracks = repo.findTracksByVehicleAndTimeRange(vehicleId, start, end);
        ZoneId enterpriseZone = timezoneService.getEnterpriseTimeZoneByVehicle(vehicleId);
        if ("geojson".equalsIgnoreCase(format)) {
            List<GeoJsonFeatureCollection> geoJsonWrapper = new ArrayList<>();
            geoJsonWrapper.add(toGeoJson(tracks, enterpriseZone));
            return geoJsonWrapper;
        } else {
            return tracks.stream()
                    .map(vehicleTrack -> new VehicleTrackDto(vehicleTrack, enterpriseZone))
                    .collect(Collectors.toList());
        }
    }

    public List<?> findVehicleTrackInInterval(Long vehicleId, ZonedDateTime start, ZonedDateTime end, String format) {
        Specification<VehicleTrack> spec = Specification.where(byVehicleId(vehicleId))
            .and(timeIntervalIn(start, end));
        List<VehicleTrack> tracks = repo.findAll(spec);
        ZoneId enterpriseZone = timezoneService.getEnterpriseTimeZoneByVehicle(vehicleId);
        if ("geojson".equalsIgnoreCase(format)) {
            List<GeoJsonFeatureCollection> geoJsonWrapper = new ArrayList<>();
            geoJsonWrapper.add(toGeoJson(tracks, enterpriseZone));
            return geoJsonWrapper;
        } else {
            return tracks.stream()
                    .map(vehicleTrack -> new VehicleTrackDto(vehicleTrack, enterpriseZone))
                    .collect(Collectors.toList());
        }
    }

    private GeoJsonFeatureCollection toGeoJson(List<VehicleTrack> tracks, ZoneId targetZone) {
        List<GeoJsonFeature> features = tracks.stream()
                .map(track -> {
                    Point point = track.getPoint();
                    GeoJsonGeometry geometry = new GeoJsonPoint(point.getY(), point.getX());
                    return new GeoJsonFeature(
                            track.getId().toString(),
                            geometry,
                            Map.of(
                                    "vehicleId", track.getVehicleId(),
                                    "recordedAt", timezoneService.getFormattedDateTimeInEnterpriseZone(track.getRecordedAt(), targetZone)
                            ));
                })
                .collect(Collectors.toList());

        return new GeoJsonFeatureCollection(features);
    }

    public void createTrack(List<VehicleTrack> points) {
        repo.saveAll(points);
    }

    public List<VehicleTrack> getStartAndEndPointByInterval(Long vehicleId, ZonedDateTime start, ZonedDateTime end) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Создаем два отдельных критерия
        VehicleTrack firstTrack = getFirstTrackAfter(cb, vehicleId, start);
        VehicleTrack lastTrack = getLastTrackBefore(cb, vehicleId, end);

        // Объединяем результаты
        List<VehicleTrack> result = new ArrayList<>();
        if (firstTrack != null) result.add(firstTrack);
        if (lastTrack != null) result.add(lastTrack);

        return result;
    }

    private VehicleTrack getFirstTrackAfter(CriteriaBuilder cb, Long vehicleId, ZonedDateTime from) {
        CriteriaQuery<VehicleTrack> query = cb.createQuery(VehicleTrack.class);
        Root<VehicleTrack> root = query.from(VehicleTrack.class);

        query.select(root)
                .where(
                        cb.and(
                                cb.greaterThanOrEqualTo(root.get("recordedAt"), from),
                                cb.equal(root.get("vehicleId"), vehicleId)
                        )
                )
                .orderBy(cb.asc(root.get("recordedAt")));

        try {
            return entityManager.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private VehicleTrack getLastTrackBefore(CriteriaBuilder cb, Long vehicleId, ZonedDateTime to) {
        CriteriaQuery<VehicleTrack> query = cb.createQuery(VehicleTrack.class);
        Root<VehicleTrack> root = query.from(VehicleTrack.class);

        query.select(root)
                .where(
                        cb.and(
                                cb.lessThanOrEqualTo(root.get("recordedAt"), to),
                                cb.equal(root.get("vehicleId"), vehicleId)
                        )
                )
                .orderBy(cb.desc(root.get("recordedAt")));

        try {
            return entityManager.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public void uploadTrack(Long vehicleId, MultipartFile file) throws IOException {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        // Парсим файл и получаем временной диапазон
        GpxTrackInfo trackInfo = parseGpxFile(file.getInputStream());

        // Проверяем пересечения с существующими треками
        checkForDateOverlaps(vehicleId, trackInfo.getStartDate(), trackInfo.getEndDate());

        // Сохраняем только если нет пересечений
        repo.saveAll(trackInfo.getTracks().stream()
                .map(t -> new VehicleTrack(vehicle.getId(), t.getPoint(), t.getRecordedAt().toInstant()))
                .collect(Collectors.toList()));

        //List<VehicleTrack> tracks = parseGpxFile(vehicle, file.getInputStream());
        //repo.saveAll(tracks);
    }

    private List<VehicleTrack> parseGpxFile(Vehicle vehicle, InputStream inputStream) throws IOException {
        List<VehicleTrack> tracks = new ArrayList<>();

        try {
            JAXBContext context = JAXBContext.newInstance(Gpx.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Gpx gpx = (Gpx) unmarshaller.unmarshal(inputStream);

            // Получаем список точек
            List<Waypoint> waypoints = gpx.getWaypoints();
            waypoints.forEach(wpt -> {
                Point point = createPoint(wpt.getLat(), wpt.getLon());
                point.setSRID(4326);
                Instant recordedAt = wpt.getRecordedAt();

                tracks.add(new VehicleTrack(vehicle.getId(), point, recordedAt));
            });

            /*for (Track track : gpx.getTrk()) {
                for (TrackSegment segment : track.getTrkseg()) {
                    for (Waypoint waypoint : segment.getTrkpt()) {
                        Point point = createPoint(waypoint.getLat(), waypoint.getLon());
                        Instant recordedAt = waypoint.getTime().toGregorianCalendar().toInstant();

                        tracks.add(new VehicleTrack(vehicle.getId(), point, recordedAt));
                    }
                }
            }*/
        } catch (JAXBException e) {
            throw new IOException("Failed to parse GPX file", e);
        }

        return tracks;
    }

    private Point createPoint(double lat, double lon) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(lon, lat);
        return geometryFactory.createPoint(coordinate);
    }

    private void checkForDateOverlaps(Long vehicleId, Instant newStart, Instant newEnd) {
        // Получаем все существующие диапазоны дат для этого vehicle
        List<Object[]> existingRanges = repo.findDateRangeByVehicle(vehicleId);

        for (Object[] range : existingRanges) {
            //Instant existingStart = (Instant) range[0];
            //Instant existingEnd = (Instant) range[1];

            Instant existingStart = ((ZonedDateTime) range[0]).toInstant();
            Instant existingEnd = ((ZonedDateTime) range[1]).toInstant();

            if (isDateRangesOverlap(newStart, newEnd, existingStart, existingEnd)) {
                throw new IllegalStateException("Новый трек пересекается по времени с существующим треком " +
                        existingStart + " - " + existingEnd);
            }
        }
    }

    private boolean isDateRangesOverlap(Instant start1, Instant end1, Instant start2, Instant end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private GpxTrackInfo parseGpxFile(InputStream inputStream) throws IOException {
        List<VehicleTrack> tracks = new ArrayList<>();
        Instant minDate = null;
        Instant maxDate = null;

        try {
            JAXBContext context = JAXBContext.newInstance(Gpx.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Gpx gpx = (Gpx) unmarshaller.unmarshal(inputStream);

            List<Waypoint> waypoints = gpx.getWaypoints();
            for (Waypoint wpt : waypoints) {
                Point point = createPoint(wpt.getLat(), wpt.getLon());
                point.setSRID(4326);
                Instant recordedAt = wpt.getRecordedAt();
                // Обновляем min/max даты
                if (minDate == null || recordedAt.isBefore(minDate)) {
                    minDate = recordedAt;
                }
                if (maxDate == null || recordedAt.isAfter(maxDate)) {
                    maxDate = recordedAt;
                }

                tracks.add(new VehicleTrack(null, point, recordedAt));
            }

        } catch (JAXBException e) {
            throw new IOException("Failed to parse GPX file", e);
        }

        return new GpxTrackInfo(tracks, minDate, maxDate);
    }

    // Вспомогательный класс для хранения информации о треке
    @Getter
    @AllArgsConstructor
    private static class GpxTrackInfo {
        private List<VehicleTrack> tracks;
        private Instant startDate;
        private Instant endDate;
    }

}
