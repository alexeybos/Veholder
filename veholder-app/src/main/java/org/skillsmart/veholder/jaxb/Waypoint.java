package org.skillsmart.veholder.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// Точка трека (широта, долгота, время)
@XmlAccessorType(XmlAccessType.FIELD)
public class Waypoint {
    @XmlAttribute
    private double lat;

    @XmlAttribute
    private double lon;

    @XmlElement(name = "extensions", namespace = "http://www.topografix.com/GPX/1/1")
    private Extensions extensions;

    public Instant getRecordedAt() {
        if (extensions == null || extensions.getRecordedAt() == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(extensions.getRecordedAt(), formatter);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    // Геттеры для остальных полей
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public Extensions getExtensions() { return extensions; }
}
