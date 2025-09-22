package org.skillsmart.veholder.controller;

import jakarta.persistence.EntityNotFoundException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.skillsmart.veholder.entity.VehicleTrack;
import org.skillsmart.veholder.entity.dto.VehicleTrackDto;
import org.skillsmart.veholder.repository.VehicleTrackRepository;
import org.skillsmart.veholder.service.VehicleTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tracks")
public class VehicleTrackRestController {

    @Autowired
    private VehicleTrackService service;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @GetMapping("/{vehicleId}")
    public ResponseEntity<?> getVehicleTrack(@PathVariable Long vehicleId,
                                                              @RequestParam ZonedDateTime start,
                                                              @RequestParam ZonedDateTime end,
                                                              @RequestParam(defaultValue = "json") String format) {
        List<?> rawList = service.getVehicleTrack(vehicleId, start, end, format);
        if ("geojson".equalsIgnoreCase(format)) return ResponseEntity.ok(rawList.getFirst());
        return ResponseEntity.ok(rawList);
    }

    @GetMapping("/only/{vehicleId}")
    public ResponseEntity<?> getVehicleTrackModel(@PathVariable Long vehicleId,
                                             @RequestParam ZonedDateTime start,
                                             @RequestParam ZonedDateTime end,
                                             @RequestParam(defaultValue = "json") String format) {
        List<?> rawList = service.findVehicleTrackInInterval(vehicleId, start, end, format);
        if ("geojson".equalsIgnoreCase(format)) return ResponseEntity.ok(rawList.getFirst());
        return ResponseEntity.ok(rawList);
    }

    @PostMapping("/{vehicleId}")
    public ResponseEntity<?> createPoints(@RequestBody List<VehicleTrack> points) {
        try {
            service.createTrack(points);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/{vehicleId}/point")
    public ResponseEntity<?> createPoint(@PathVariable Long vehicleId, @RequestBody Map<String, Object> vPoint) {
        Point point = geometryFactory.createPoint(new Coordinate((Double) vPoint.get("lon"), (Double) vPoint.get("lat")));
        point.setSRID(4326);
        VehicleTrack trackPoint = new VehicleTrack();
        trackPoint.setPoint(point);
        trackPoint.setVehicleId(vehicleId);
        trackPoint.setRecordedAt(ZonedDateTime.parse((String) vPoint.get("recordedAt")));
        try {
            service.createTrack(Collections.singletonList(trackPoint));
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/{vehicleId}/track/load")
    public ResponseEntity<?> uploadTrack(
            @PathVariable Long vehicleId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            service.uploadTrack(vehicleId, file);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to process GPX file");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }
}
