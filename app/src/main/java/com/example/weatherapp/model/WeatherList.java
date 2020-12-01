package com.example.weatherapp.model;

import java.util.ArrayList;
import java.util.List;

public class WeatherList {

    private static List<Weather> weathers;

    // private constructor to force the use of getInstance() to get an/the object
    private WeatherList() {}

    public static List<Weather> getInstance()
    {
        if (weathers == null)
            weathers = new ArrayList<>();
        return weathers;
    }
}
