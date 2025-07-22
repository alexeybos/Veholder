package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/trips")
public class TripRestController {

    @Autowired
    private TripService service;

    @GetMapping("/{vehicleId}")
    public ResponseEntity<?> getVehicleTripsTrack(@PathVariable Long vehicleId,
                                             @RequestParam ZonedDateTime start,
                                             @RequestParam ZonedDateTime end,
                                             @RequestParam(defaultValue = "json") String format) {
        List<?> rawList = service.getTripsTracksByTimeRange(vehicleId, start, end, format);
        if ("geojson".equalsIgnoreCase(format)) return ResponseEntity.ok(rawList.getFirst());
        return ResponseEntity.ok(rawList);
    }

    @GetMapping("/only/{vehicleId}")
    public ResponseEntity<?> getVehicleTrips(@PathVariable Long vehicleId,
                                                  @RequestParam ZonedDateTime start,
                                                  @RequestParam ZonedDateTime end,
                                                  @RequestParam(defaultValue = "json") String format) {
        List<?> rawList = service.findTripsWithinInterval(vehicleId, start, end);
        return ResponseEntity.ok(rawList);
    }

    @GetMapping("/info/{vehicleId}")
    public ResponseEntity<?> getVehicleTripsInfo(@PathVariable Long vehicleId,
                                             @RequestParam ZonedDateTime start,
                                             @RequestParam ZonedDateTime end) throws Exception {
        List<?> rawList = service.getTripsInfo(vehicleId, start, end);
        return ResponseEntity.ok(rawList);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createTrip(@RequestParam Long vehicleId,
                                        @RequestParam ZonedDateTime start,
                                        @RequestParam ZonedDateTime end) {
        try {
            service.createTrip(vehicleId, start, end);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Access Denied",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }

    }
}
