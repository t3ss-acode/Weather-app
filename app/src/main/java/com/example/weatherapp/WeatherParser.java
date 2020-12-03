package com.example.weatherapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.weatherapp.model.Coordinates;
import com.example.weatherapp.model.Weather;
import com.example.weatherapp.model.WeatherList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherParser extends AsyncTask<JSONObject, Void, List<Weather>> {

    private static final String LOG_TAG = WeatherParser.class.getSimpleName();
    private static final String
            TIMESERIES = "timeSeries",
            VALIDTIME = "validTime",
            PARAMTERES = "parameters",
            TEMPERATURE = "t",
            CLOUDCOVERAGE = "tcc_mean",
            APPROVEDTIME = "approvedTime";

    private WeakReference<List<Weather>> mWeatherList;
    private WeakReference<WeatherAdapter> mWeatherAdapter;
    private WeakReference<TextView> mLastApprovedTime;
    private WeakReference<TextView> mLonTextView;
    private WeakReference<TextView> mLatTextView;
    private String approvedTime;
    private String lastLon = "";
    private String lastLat = "";

    private StringBuilder builder;





    //Get view elements when that comes
    WeatherParser(WeatherAdapter adapter, TextView lastApproved, TextView lonView, TextView latView, String lonStr, String latStr) {
        this.mWeatherList = new WeakReference<>(WeatherList.getInstance());
        mWeatherAdapter = new WeakReference<>(adapter);
        this.mLastApprovedTime = new WeakReference<>(lastApproved);
        this.mLonTextView = new WeakReference<>(lonView);
        this.mLatTextView = new WeakReference<>(latView);

        lastLon = lonStr;
        lastLat = latStr;

        builder = new StringBuilder();
    }

    //Parse the JSON data
    @Override
    protected List<Weather> doInBackground(JSONObject... jsonObjects) {
        try {
            //clear the list and add the new items to the list
            mWeatherList.get().clear();
            mWeatherList.get().addAll(getWeatherList(jsonObjects[0]));
            approvedTime = getApprovedTime(jsonObjects[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mWeatherList.get();
    }

    //Update the UI with the parsed data
    @Override
    protected void onPostExecute(List<Weather> weathers) {
        super.onPostExecute(weathers);

        Coordinates.setApprovedTimeString(approvedTime);
        Coordinates.setApprovedTimeMillis(System.currentTimeMillis());

        mWeatherAdapter.get().notifyDataSetChanged();

        mLastApprovedTime.get().setText(approvedTime);
        mLonTextView.get().setText(lastLon);
        mLatTextView.get().setText(lastLat);
    }


    private String getApprovedTime(JSONObject jsonObject) throws JSONException {

        return editTimeString(jsonObject.getString(APPROVEDTIME));
    }


    private String editTimeString(String str) {
        String timeStr = str.replace("T", " ");
        timeStr = timeStr.replace("Z", "");
        return timeStr;
    }

    private Weather getWeather(JSONObject jsonObj) throws JSONException {

        builder.setLength(0);

        String timeStr;
        double temperature = Integer.MIN_VALUE;
        int cloudCoverage = Integer.MIN_VALUE;

        //Get time and date this weather takes place
        // Remove the T and Z from 2020-11-22T19:00:00Z
        builder.append(jsonObj.getString(VALIDTIME));
        builder.setCharAt(10, ' ');
        builder.setCharAt(19, ' ');


        //timeStr = editTimeString(timeStr);

        //Parameter arrays in the object
        JSONArray parameters = jsonObj.getJSONArray(PARAMTERES);

        //find parameters t and tcc_mean that hold the temperature value and the cloud coverage
        for (int j = 0; j < parameters.length(); j++) {
            JSONObject parameter = parameters.getJSONObject(j);

            //temperature
            if(parameter.getString("name").equals(TEMPERATURE)) {
                JSONArray values = parameter.getJSONArray("values");
                temperature = values.getDouble(0);
            }
            //cloud coverage
            else if(parameter.getString("name").equals(CLOUDCOVERAGE)) {
                JSONArray values = parameter.getJSONArray("values");
                cloudCoverage = values.getInt(0);
            }
        }
        return new Weather(builder.toString(), temperature, cloudCoverage);
    }

    private List<Weather> getWeatherList(JSONObject weatherObj) throws JSONException {
        Log.d(LOG_TAG, "JSON: " + weatherObj);

        List<Weather> weathers = new ArrayList<>();

        //Get to the array holding the weather data
        JSONArray timeSeries = weatherObj.getJSONArray(TIMESERIES);

        //One object for every hour for 10 days
        for(int i = 0; i < timeSeries.length(); i++) {
            JSONObject parametersAtTime = timeSeries.getJSONObject(i);
            weathers.add(getWeather(parametersAtTime));
        }

        return weathers;
    }
}
