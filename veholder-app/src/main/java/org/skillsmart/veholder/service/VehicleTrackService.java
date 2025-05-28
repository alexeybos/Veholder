package org.skillsmart.veholder.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.locationtech.jts.geom.Point;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.VehicleTrack;
import org.skillsmart.veholder.entity.dto.VehicleTrackDto;
import org.skillsmart.veholder.repository.VehicleTrackRepository;
import org.skillsmart.veholder.utils.GeoJsonFeature;
import org.skillsmart.veholder.utils.GeoJsonFeatureCollection;
import org.skillsmart.veholder.utils.GeoJsonGeometry;
import org.skillsmart.veholder.utils.GeoJsonPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

}
