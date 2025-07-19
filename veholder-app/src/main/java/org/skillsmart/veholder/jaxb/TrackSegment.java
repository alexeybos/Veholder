package org.skillsmart.veholder.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

// Сегмент трека (содержит точки)
@XmlAccessorType(XmlAccessType.FIELD)
public class TrackSegment {
    @XmlElement(name = "trkpt", namespace = "http://www.topografix.com/GPX/1/1")
    private List<Waypoint> trkpt;

    public List<Waypoint> getTrkpt() {
        return trkpt;
    }
}
