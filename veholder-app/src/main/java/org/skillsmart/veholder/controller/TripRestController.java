package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

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
}
