package com.example.weatherapp.serialize;

import com.example.weatherapp.model.Coordinates;
import com.example.weatherapp.model.Weather;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SerializeToFile {

    private static final String FILENAME = "saved_weather";

    public SerializeToFile() {
    }

    public static boolean SaveWeather(List<Weather> weatherList, String approvedTime, String lat, String lon, File directory) {
        ArrayList<Object> data = new ArrayList<>();

        data.add(Coordinates.getApprovedTimeMillis());
        data.add(Coordinates.getApprovedTimeString());
        data.add(lat);
        data.add(lon);
        data.add(weatherList);

        try {
            File file = new File(directory, FILENAME);
            file.createNewFile(); // if file already exists will do nothing
            FileOutputStream fileOut = new FileOutputStream(file);

            //write the list to the file
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(data);

            objectOut.close();
            fileOut.close();
            System.out.println("The Object  was succesfully written to a file");
            System.out.println(directory);
            System.out.println("serialize list: " + data.toString());

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
