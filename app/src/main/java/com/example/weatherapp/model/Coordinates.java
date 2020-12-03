package com.example.weatherapp.model;

public class Coordinates {
    private static Long approvedTimeMillis;
    private static String approvedTimeString;
    //Between -90 and 90
    private static Float latitude;
    //Between -180 and 180
    private static Float longitude;


    private Coordinates() {}

    public static Long getApprovedTimeMillis() {
        return approvedTimeMillis;
    }

    public static void setApprovedTimeMillis(Long millis) {
        approvedTimeMillis = millis;
    }


    public static String getApprovedTimeString() {
        return approvedTimeString;
    }

    public static void setApprovedTimeString(String str) {
        approvedTimeString = str;
    }


    public static Float getLatitude() {
        return latitude;
    }

    public static void setLatitude(Float lat) {
        latitude = lat;
    }


    public static Float getLongitude() {
        return longitude;
    }

    public static void setLongitude(Float lon) {
        longitude = lon;
    }
}
