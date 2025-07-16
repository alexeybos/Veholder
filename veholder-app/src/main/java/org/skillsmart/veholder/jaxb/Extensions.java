package org.skillsmart.veholder.jaxb;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "extensions")
public class Extensions {
    @XmlElement(name = "id", namespace = "ogr")
    private String id;

    @XmlElement(name = "recordedAt", namespace = "http://osgeo.org/gdal")
    private String recordedAt;

    @XmlElement(name = "vehicleId", namespace = "ogr")
    private String vehicleId;

    // Геттеры и сеттеры
    public String getRecordedAt() {
        return recordedAt;
    }
}
