package com.example.weatherapp.serialize;

import android.os.AsyncTask;
import android.widget.TextView;

import com.example.weatherapp.WeatherAdapter;
import com.example.weatherapp.model.Coordinates;
import com.example.weatherapp.model.Weather;
import com.example.weatherapp.model.WeatherList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeserializeFromFile extends AsyncTask<File , Void, ArrayList<Object>> {

    private static final String FILENAME = "saved_weather";
    private WeakReference<List<Weather>> mWeatherList;
    private WeakReference<WeatherAdapter> mWeatherAdapter;
    private WeakReference<TextView> mLastApprovedTime;
    private WeakReference<TextView> mLonTextView;
    private WeakReference<TextView> mLatTextView;

    public DeserializeFromFile(WeatherAdapter adapter, TextView lastApproved, TextView lonView, TextView latView) {
        this.mWeatherList = new WeakReference<>(WeatherList.getInstance());
        mWeatherAdapter = new WeakReference<>(adapter);
        this.mLastApprovedTime = new WeakReference<>(lastApproved);
        this.mLonTextView = new WeakReference<>(lonView);
        this.mLatTextView = new WeakReference<>(latView);
    }

    private ArrayList<Object> loadWeather(File directory) {
        ArrayList<Object> deserializedData = new ArrayList<>();

        try {
            File file = new File(directory, FILENAME);
            System.out.println("file length: " + file.length());
            //Get access to the file
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            //Read the list from the file
            deserializedData = (ArrayList<Object>) objectIn.readObject();

            objectIn.close();
            fileIn.close();

        } catch(IOException ex) {
            System.out.println("IOException is caught");
            System.out.println("Unable to read saved data");
        } catch(ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");
        }

        System.out.println("Object has been deserialized");
        return deserializedData;
    }

    @Override
    protected ArrayList<Object> doInBackground(File... files) {
        return loadWeather(files[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<Object> deserializedData) {
        super.onPostExecute(deserializedData);

        //Fill the variables with the data
        System.out.println("size: " + deserializedData.size());
        System.out.println("tostring: " + deserializedData.toString());

        if(deserializedData.size() > 0) {
            Long approvedTimeMillis = (Long) deserializedData.get(0);
            String approvedTimeString = (String) deserializedData.get(1);

            String lat = (String) deserializedData.get(2);
            String lon = (String) deserializedData.get(3);

            mWeatherList.get().clear();
            mWeatherList.get().addAll((List<Weather>) deserializedData.get(4));

            Coordinates.setApprovedTimeMillis(approvedTimeMillis);
            Coordinates.setApprovedTimeString(approvedTimeString);
            Coordinates.setLatitude(lat);
            Coordinates.setLongitude(lon);

            mWeatherAdapter.get().notifyDataSetChanged();
            mLastApprovedTime.get().setText(approvedTimeString);
            mLonTextView.get().setText(lon);
            mLatTextView.get().setText(lat);

            System.out.println("Views have been filled");
        }else {
            System.out.println("No deserialized object");
        }
    }
}
