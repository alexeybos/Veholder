package org.skillsmart.veholder.service;

import com.vladmihalcea.hibernate.type.range.Range;
import org.skillsmart.veholder.entity.dto.TripDTO;
import org.skillsmart.veholder.entity.dto.TripDatesDTO;
import org.skillsmart.veholder.repository.TripRepository;
import org.skillsmart.veholder.utils.GeoJsonFeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository repo;

    @Autowired
    private VehicleTrackService trackService;

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
}
