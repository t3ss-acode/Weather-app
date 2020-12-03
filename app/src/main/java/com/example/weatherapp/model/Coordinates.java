package com.example.weatherapp.model;

public class Coordinates {
    private static Long approvedTimeMillis;
    private static String approvedTimeString;
    private static String latitude;
    private static String longitude;


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


    public static String getLatitude() {
        return latitude;
    }

    public static void setLatitude(String lat) {
        latitude = lat;
    }


    public static String getLongitude() {
        return longitude;
    }

    public static void setLongitude(String lon) {
        longitude = lon;
    }
}
