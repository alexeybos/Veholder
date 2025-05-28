package org.skillsmart.vehicleutils.util.entity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class TrackPoint {
    double lat;
    double lon;
    ZonedDateTime time;

    public TrackPoint(double lat, double lon, ZonedDateTime time) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }
}
