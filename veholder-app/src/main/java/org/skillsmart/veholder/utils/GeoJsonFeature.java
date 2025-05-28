package org.skillsmart.veholder.utils;

import java.util.Map;

public class GeoJsonFeature {
    private final String type = "Feature";
    private final String id;
    private final GeoJsonGeometry geometry;
    private final Map<String, Object> properties;

    public GeoJsonFeature(String id, GeoJsonGeometry geometry, Map<String, Object> properties) {
        this.id = id;
        this.geometry = geometry;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public GeoJsonGeometry getGeometry() {
        return geometry;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
