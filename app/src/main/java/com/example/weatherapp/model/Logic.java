package com.example.weatherapp.model;

import android.util.Log;


public class Logic {

    private static final String LOG_TAG = Logic.class.getSimpleName();
    private static final String WEATHER_URL_BASE = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/";



    public boolean timeForNewDownload(long timeLimit) {
        if(Coordinates.getApprovedTimeMillis() == null) {
            Log.d(LOG_TAG, "No time to compare to");
            return false;
        }else if(System.currentTimeMillis() - Coordinates.getApprovedTimeMillis() > timeLimit) {
            Log.d(LOG_TAG, "Time to update");
            return true;
        }else {
            Log.d(LOG_TAG, "not enough time has passed");
            return false;
        }
    }


    public boolean validInput(String lon, String lat) {
        try {
            String dot = ".";
            float lonFloat = Float.parseFloat(lon);
            float latFloat = Float.parseFloat(lat);

            String lastLon = Character.toString(lon.charAt(lon.length() - 1));
            String lastLat = Character.toString(lat.charAt(lat.length() - 1));

            if (lon.contains(dot) && !lastLon.equals(dot) && lat.contains(dot) && !lastLat.equals(dot)) {
                if(withinRange(lonFloat, latFloat)) {
                    return true;
                }else {
                    Log.d(LOG_TAG, "Outside of range");
                    return false;
                }
            } else {
                Log.d(LOG_TAG, "not a float value");
                return false;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Unable to parse lon and lat input");
            return false;
        }
    }

    private boolean withinRange(Float lon, Float lat) {
        //Latitude between -90 and 90
        //Longitude between -180 and 180
        if(lon < -180 || lon > 180) {
            return false;
        }
        if(lat < -90 || lat > 90) {
            return false;
        }

        return true;
    }


    public String createURL(String lon, String lat) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.delete(0, stringBuilder.length());

        // URL looks like this
        // https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/15.000/lat/60.000/data.json";
        stringBuilder.append(WEATHER_URL_BASE);
        stringBuilder.append(lon);
        stringBuilder.append("/lat/");
        stringBuilder.append(lat);
        stringBuilder.append("/data.json");

        return stringBuilder.toString();
    }
}
