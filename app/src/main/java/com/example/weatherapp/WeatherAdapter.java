package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.model.Weather;
import com.example.weatherapp.model.WeatherList;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private List<Weather> weathers = WeatherList.getInstance();
    private static final String DEGREES = "\u00B0C";


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTextView;
        public TextView cloudCoverageTextView;
        public TextView temperatureTextView;
        public ViewHolder(View v) {
            super(v);
        }
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        final ViewHolder vh = new ViewHolder(itemView);

        vh.timeTextView = itemView.findViewById(R.id.date_and_time_text);
        vh.cloudCoverageTextView = itemView.findViewById(R.id.cloud_coverage_text);
        vh.temperatureTextView = itemView.findViewById(R.id.temperature_text);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int position) {
        Weather weather = weathers.get(position);

        //set the weather data for every view item
        vh.timeTextView.setText(weather.getTime());
        vh.cloudCoverageTextView.setText(translateCloudCoverage(weather.getCloudCoverage()));
        vh.temperatureTextView.setText(Double.toString(weather.getTemperature()) + DEGREES);
    }

    private String translateCloudCoverage(int cloudNr) {
        String cloudStr;
        //translate the int representing the cloud coverage to text
        switch(cloudNr) {
            case 1:
                cloudStr = "Nearly clear skies";
                break;
            case 2:
                cloudStr = "Kind of clear skies";
                break;
            case 3:
                cloudStr = "Variable cloudiness";
                break;
            case 4:
                cloudStr = "Half clear skies";
                break;
            case 5:
                cloudStr = "Kind of cloudy skies";
                break;
            case 6:
                cloudStr = "Cloudy skies";
                break;
            case 7:
                cloudStr = "Very cloudy skies";
                break;
            case 8:
                cloudStr = "Overcast";
                break;
            default:
                cloudStr = "Clear skies";
        }
        return cloudStr;
    }

    @Override
    public int getItemCount() {
        return weathers.size();
    }
}
