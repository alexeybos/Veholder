package org.skillsmart.veholder.controller;

import org.locationtech.jts.geom.Point;
import org.skillsmart.veholder.entity.VehicleTrack;
import org.skillsmart.veholder.entity.dto.VehicleTrackDto;
import org.skillsmart.veholder.repository.VehicleTrackRepository;
import org.skillsmart.veholder.service.VehicleTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tracks")
public class VehicleTrackRestController {

    @Autowired
    private VehicleTrackService service;

    @GetMapping("/{vehicleId}")
    public ResponseEntity<?> getVehicleTrack(@PathVariable Long vehicleId,
                                                              @RequestParam ZonedDateTime start,
                                                              @RequestParam ZonedDateTime end,
                                                              @RequestParam(defaultValue = "json") String format) {
        List<?> rawList = service.getVehicleTrack(vehicleId, start, end, format);
        if ("geojson".equalsIgnoreCase(format)) return ResponseEntity.ok(rawList.getFirst());
        return ResponseEntity.ok(rawList);
    }
}
