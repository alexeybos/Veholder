package org.skillsmart.veholder.jaxb;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "gpx", namespace = "http://www.topografix.com/GPX/1/1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Gpx {
    // Для точек (waypoints)
    @XmlElement(name = "wpt", namespace = "http://www.topografix.com/GPX/1/1")
    private List<Waypoint> waypoints;

    // Для треков (если понадобятся в будущем)
    @XmlElement(name = "trk", namespace = "http://www.topografix.com/GPX/1/1")
    private List<Track> tracks;

    // Геттеры
    public List<Waypoint> getWaypoints() {
        if (waypoints == null) {
            waypoints = new ArrayList<>();
        }
        return waypoints;
    }

    // Геттеры и сеттеры
    public List<Track> getTrk() {
        return tracks;
    }
}
