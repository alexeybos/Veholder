package org.skillsmart.veholder.service;

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

@Service
public class VehicleTrackService {

    @Autowired
    private VehicleTrackRepository repo;
    @Autowired
    private TimezoneService timezoneService;

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

}
