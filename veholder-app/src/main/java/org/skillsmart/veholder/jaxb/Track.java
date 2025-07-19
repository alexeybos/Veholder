package org.skillsmart.veholder.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Track {
    @XmlElement(name = "trkseg", namespace = "http://www.topografix.com/GPX/1/1")
    private List<TrackSegment> trkseg;
    // Геттеры и сеттеры


    public List<TrackSegment> getTrkseg() {
        return trkseg;
    }

    public void setTrkseg(List<TrackSegment> trkseg) {
        this.trkseg = trkseg;
    }
}
