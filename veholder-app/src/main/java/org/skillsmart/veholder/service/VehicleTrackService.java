package org.skillsmart.veholder.service;

import org.skillsmart.veholder.repository.VehicleTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.List;

public class VehicleTrackService {

    @Autowired
    private VehicleTrackRepository repo;

    List<?> getVehicleTrack(Long vehicleId, ZonedDateTime start, ZonedDateTime end, String format) {
        
    }
}
