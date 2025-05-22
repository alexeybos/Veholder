package org.skillsmart.veholder.utils;

public class GeoJsonPoint implements GeoJsonGeometry {
    private final String type = "Point";
    private final double[] coordinates;

    public GeoJsonPoint(double longitude, double latitude) {
        this.coordinates = new double[]{longitude, latitude};
    }

    @Override
    public String getType() {
        return this.type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }
}
