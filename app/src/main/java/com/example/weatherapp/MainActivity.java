package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.model.Weather;
import com.example.weatherapp.model.WeatherList;
import com.example.weatherapp.serialize.DeserializeFromFile;
import com.example.weatherapp.serialize.SerializeToFile;

import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String WEATHER_URL_BASE = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/";
    private static final String FILENAME = "saved_weather";
    private static final long NO_CONNECTION_TIMEOUT = 600_000;
    private static final long HOURLY_REFRESH_TIMEOUT = 3_600_000;


    // data
    private List<Weather> weatherList;
    private String lastLon = "";
    private String lastLat = "";
    private StringBuilder stringBuilder;
    private boolean automaticDownload = false;

    // ui
    private TextView approvedTimeTextView;
    private TextView lonTextView;
    private TextView latTextView;
    private TextView loadedDataTextView;
    private WeatherAdapter mWeatherAdapter;
    private EditText mLonInput;
    private EditText mLatInput;

    // Volley
    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // data
        weatherList = WeatherList.getInstance(); // get the singleton list
        stringBuilder = new StringBuilder();

        // ui
        approvedTimeTextView = findViewById(R.id.approved_time);
        lonTextView = (TextView) findViewById(R.id.lon_text_view);
        latTextView = (TextView) findViewById(R.id.lat_text_view);
        loadedDataTextView = (TextView) findViewById(R.id.loaded_data_view);
        mLonInput = (EditText) findViewById(R.id.lon_input);
        mLatInput = (EditText) findViewById(R.id.lat_input);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter
        mWeatherAdapter = new WeatherAdapter();
        recyclerView.setAdapter(mWeatherAdapter);

        // Volley
        mRequestQueue = Volley.newRequestQueue(this);

        //Keep content of screen rotates
        if (savedInstanceState != null) {
            approvedTimeTextView.setText(savedInstanceState.getString("approved_text"));
            latTextView.setText(savedInstanceState.getString("lat_text"));
            lonTextView.setText(savedInstanceState.getString("lon_text"));

            if (savedInstanceState.getBoolean("loaded_visible")) {
                loadedDataTextView.setText(savedInstanceState.getString("reply_text"));
                loadedDataTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //all kolla nätverk här istället för i onCreate
        //läs bara från fil om det finns en fil
        //Om vi inte har en fil och inte har nätverk
        //I den metoden som hämta data, så fort vi hämtat data så skriv ner det i filen


        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        //If there is internet connection, get fresh data if an hour has passed
        // or if it is loaded data then get if 10 min has passed
        if (networkInfo != null && networkInfo.isConnected()) {
            if(!approvedTimeTextView.getText().equals("Approved time")) {
                if(timeForNewDownload(HOURLY_REFRESH_TIMEOUT)) {
                    automaticDownload = true;
                    downloadWeather(null);
                    Log.d(LOG_TAG, "time to update after an hour");
                }else {
                    Log.d(LOG_TAG, "an hour hasn't passed");
                }
            }else if (loadedDataTextView.getVisibility() == View.VISIBLE) {
                if (timeForNewDownload(NO_CONNECTION_TIMEOUT)) {
                    automaticDownload = true;
                    downloadWeather(null);
                    Log.d(LOG_TAG, "time to update after loaded data");
                }else {
                    Log.d(LOG_TAG, "Wait some more loaded");
                }
            }
        } else {
            File file = new File(this.getFilesDir(), FILENAME);
            if (file.length() > 0) {
                loadedDataTextView.setVisibility(View.VISIBLE);
                new DeserializeFromFile(mWeatherAdapter, approvedTimeTextView, lonTextView, latTextView).execute(this.getFilesDir());
            } else {
                showToast("No stored data to load");
            }
        }
    }

    private boolean timeForNewDownload(long timeLimit) {

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date lastDownloadDate = formatter.parse(approvedTimeTextView.getText().toString());
            Log.d(LOG_TAG, System.currentTimeMillis() + " ," + lastDownloadDate.getTime());
            if ((System.currentTimeMillis() - lastDownloadDate.getTime()) > timeLimit) { // 10 min
                Log.d(LOG_TAG, "Time to update");
                return true;
            }
            Log.d(LOG_TAG, "not enough time has passed");
            return false;
        } catch (ParseException e) {
            Log.d(LOG_TAG, "not able to parse date");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Volley, cancel pending requests
        mRequestQueue.cancelAll(this);
        //volley stop pending request. Ta bort resten i kön
        //påmin under redovisningen om lab parter. Vilka som också redovisat lab 1 och söker partner
        getApplicationContext();
        if (weatherList.size() > 0) {
            SerializeToFile.SaveWeather(weatherList, approvedTimeTextView.getText().toString(),
                    latTextView.getText().toString(), lonTextView.getText().toString(), this.getFilesDir());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("approved_text", approvedTimeTextView.getText().toString());
        outState.putString("lat_text", latTextView.getText().toString());
        outState.putString("lon_text", lonTextView.getText().toString());

        if (loadedDataTextView.getVisibility() == View.VISIBLE) {
            outState.putBoolean("loaded_visible", true);
            outState.putString("reply_text", loadedDataTextView.getText().toString());
        }
    }

    public void downloadWeather(View view) {
        //Get the values for lat and lon depending on what called the download
        String lonQueryString;
        String latQueryString;
        if (automaticDownload) {
            lonQueryString = lonTextView.getText().toString();
            latQueryString = latTextView.getText().toString();
        } else {
            lonQueryString = mLonInput.getText().toString();
            latQueryString = mLatInput.getText().toString();
        }


        // Hide the keyboard when the button is pushed.
        if (!automaticDownload) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        automaticDownload = false;

        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            if (lonQueryString.length() != 0 && latQueryString.length() != 0) {
                if (!inputInvalid(lonQueryString, latQueryString)) {
                    //Create the url with the given lon and lat
                    String url = createURL(lonQueryString, latQueryString);

                    //update lastLon and lastLat
                    lastLon = lonQueryString;
                    lastLat = latQueryString;
                    postVolleyRequest(url);
                    Log.d(LOG_TAG, "downloading...");
                } else {
                    showToast("Invalid input");
                    Log.d(LOG_TAG, "Invalid input");
                }
            } else {
                showToast("Please enter longitude and latitude");
                Log.d(LOG_TAG, "Please enter longitude and latitude");
            }
        } else {
            showToast("Unable to connect to the internet");
            Log.d(LOG_TAG, "Unable to connect to the internet");
        }
    }

    private boolean inputInvalid(String lon, String lat) {
        try {
            String dot = ".";
            float lonFloat = Float.parseFloat(lon);
            float latFloat = Float.parseFloat(lat);

            String lastLon = Character.toString(lon.charAt(lon.length() - 1));
            String lastLat = Character.toString(lat.charAt(lat.length() - 1));

            if (lonFloat < 0 || latFloat < 0) {
                return true;
            }
            //
            if (lon.contains(dot) && !lastLon.equals(dot) && lat.contains(dot) && !lastLat.equals(dot)) {
                return false;
            } else {
                Log.d(LOG_TAG, "not a float value");
                return true;
            }
        } catch (Exception e) {
            showToast("Invalid input");
            Log.d(LOG_TAG, "Unable to parse lon and lat input");
            return true;
        }
    }

    private String createURL(String lon, String lat) {
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

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void postVolleyRequest(String url) {
        JsonObjectRequest weatherRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                responseListener,
                errorListener);
        weatherRequest.setTag(this); // mark this request, might have to cancel it in onStop
        mRequestQueue.add(weatherRequest); // Volley processes the request on a worker thread
    }

    // executed on main thread
    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject responseObj) {
            Log.d(LOG_TAG, "got response");

            try {
                Log.d(LOG_TAG, "onRepsonse: " + responseObj);

                new WeatherParser(mWeatherAdapter, approvedTimeTextView, lonTextView, latTextView, lastLon, lastLat).execute(responseObj);
                loadedDataTextView.setVisibility(View.INVISIBLE);

                mLonInput.setText(lastLon);
                mLatInput.setText(lastLat);


                // cancel pending requests
                mRequestQueue.cancelAll(this);
            } catch (Exception e) {
                showToast("Unable to parse data");
                Log.i("error while parsing", e.toString());
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            showToast("Volley error");
            Log.i("Volley error", error.toString());
        }
    };
}