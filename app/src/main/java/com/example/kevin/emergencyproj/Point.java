package com.example.kevin.emergencyproj;

public class Point {

    interface Type {
        String EARTHQUAKE = "earthquake";
        String FLOOD = "flood";
        String WILDFIRE = "wildfire";
        String TORNADO = "tornado";
        String BLIZZARD = "blizzard";
        String LANDSLIDE = "landslide";

        String RESPONDER = "responder";
    }

    private double latitude;
    private double longitude;
    private String type;

    public Point(double lat, double lon, String t) {
        latitude = lat;
        longitude = lon;
        type = t;
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public String getType() { return type; }
}
