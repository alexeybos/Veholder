package org.skillsmart.veholder.entity.dto;

import org.skillsmart.veholder.entity.Trip;
import org.skillsmart.veholder.entity.VehicleTrack;

public class TripImportResult {
    private final Trip trip;
    private final VehicleTrack trackStart;
    private final VehicleTrack trackEnd;

    public TripImportResult(Trip trip, VehicleTrack trackStart, VehicleTrack trackEnd) {
        this.trip = trip;
        this.trackStart = trackStart;
        this.trackEnd = trackEnd;
    }

    // Геттеры


    public Trip getTrip() {
        return trip;
    }

    public VehicleTrack getTrackStart() {
        return trackStart;
    }

    public VehicleTrack getTrackEnd() {
        return trackEnd;
    }
}
