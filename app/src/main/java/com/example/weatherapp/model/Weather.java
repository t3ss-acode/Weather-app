package com.example.weatherapp.model;

public class Weather implements java.io.Serializable{

    private String time;
    private double temperature;
    //A number 0-8 for which image to use
    private int cloudCoverage;

    public Weather(String time, double temperature, int cloudCoverage) {
        this.time = time;
        this.temperature = temperature;
        this.cloudCoverage = cloudCoverage;
    }

    public String getTime() { return time; }

    public double getTemperature() { return temperature; }

    public int getCloudCoverage() { return cloudCoverage; }

    @Override
    public String toString() {
        return "Weather{" +
                "time='" + time + '\'' +
                ", temperature=" + temperature +
                ", cloudCoverage=" + cloudCoverage +
                '}';
    }
}
