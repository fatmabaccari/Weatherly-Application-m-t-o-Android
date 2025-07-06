package com.example.weatherly;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherListModel extends ArrayAdapter<WeatherItem> {

    private List<WeatherItem> listItems;
    private int resource;

    // A map to store image resources based on weather types
    public static Map<String, Integer> images = new HashMap<>();

    // Static block to populate image resources for weather types
    static {
        images.put("Clear", R.drawable.clear);
        images.put("Clouds", R.drawable.clouds);
        images.put("Rain", R.drawable.rain);
        images.put("Thunderstorm", R.drawable.thunderstormspng);
    }

    // Constructor
    public WeatherListModel(@NonNull Context context, int resource, List< WeatherItem> data) {
        super(context, resource, data);
        this.listItems = data;
        this.resource = resource;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i("MeteoListModel", "Position: " + position);

        View listItem = convertView;

        // If the convertView is null, inflate a new view
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }

        // Find the views in the layout
        ImageView imageView = listItem.findViewById(R.id.imageView);
        TextView textViewTempMax = listItem.findViewById(R.id.textViewTempMAX);
        TextView textViewTempMin = listItem.findViewById(R.id.textViewTempMin);
        TextView textViewPression = listItem.findViewById(R.id.textViewPression);
        TextView textViewHumidite = listItem.findViewById(R.id.textViewHumidite);
        TextView textViewDate = listItem.findViewById(R.id.textViewDate);

        // Get the current weather item
        WeatherItem currentItem = listItems.get(position);

        // Set the image resource if the key is valid
        String key = currentItem.image;
        if (key != null) {
            Integer imageResource = images.get(key);
            if (imageResource != null) {
                imageView.setImageResource(imageResource);
            }
        }

        // Set the text views with weather data
        textViewTempMax.setText(String.valueOf(currentItem.tempMax) + " °C");
        textViewTempMin.setText(String.valueOf(currentItem.tempMin) + " °C");
        textViewPression.setText(String.valueOf(currentItem.pression) + " hPa");
        textViewHumidite.setText(String.valueOf(currentItem.humidite) + " %");

        textViewDate.setText(String.valueOf(currentItem.date));

        return listItem;
    }
}

