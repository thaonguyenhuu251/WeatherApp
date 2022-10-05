package com.example.weatherapp;

import static com.example.weatherapp.R.string.time_weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    BottomSheetBehavior bottomSheetBehavior;
    LinearLayout layoutBottomSheet;
    LinearLayoutManager layoutManager;
    private ArrayList<WeatherTimeModel> weatherTimeModelArrayList;
    private WeatherTimeAdapter weatherTimeAdapter;
    private LocationManager locationManager;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        setBottomSheetBehavior();
        setDataTime();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSION_CODE
            );
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(), location.getLatitude());
        getWeatherInformation(cityName);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted ...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide the permissions ...", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for (Address adr: addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Toast.makeText(this, "User City Not Found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }


    private void setBottomSheetBehavior() {
        layoutBottomSheet = binding.layoutBottomsheet.bottomSheetWeather;
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        binding.layoutBottomsheet.txtTitle.setOnClickListener(v->{
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

    }

    private void setDataTime() {
        weatherTimeModelArrayList = new ArrayList<>();
        weatherTimeAdapter = new WeatherTimeAdapter(this, weatherTimeModelArrayList);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        binding.includeDayWeather.rcvWeatherDetail.setLayoutManager(layoutManager);
        binding.includeDayWeather.textTitleDetail.setText(time_weather);
        binding.includeDayWeather.rcvWeatherDetail.setAdapter(weatherTimeAdapter);
    }

    private void getWeatherInformation(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=5a95dd7a295241668db101146220310&q=HaNoi&days=1&aqi=yes&alerts=yes";
        binding.includeInformationWeather.txtLocal.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            weatherTimeModelArrayList.clear();
            try {
                String locationName = response.getJSONObject("location").getString("name");
                binding.includeInformationWeather.txtLocal.setText(locationName);

                double temperature = response.getJSONObject("current").getDouble("temp_c");
                binding.includeInformationWeather.txtTemplate.setText(String.valueOf(temperature));


                int isDay = response.getJSONObject("current").getInt("is_day");
                String conditionText = response.getJSONObject("current").getJSONObject("condition").getString("text");
                String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                Picasso.get().load("http:".concat(conditionIcon)).into(binding.includeInformationWeather.imageView);

                if (isDay == 1) {
                    //morning
                    //Picasso.get().load("").into(binding.includeInformationWeather.imageView);
                } else {

                }

                JSONObject forecastObj = response.getJSONObject("forecast");
                JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);

                JSONArray hourArray = forecast0.getJSONArray("hour");

                for(int i = 0; i < hourArray.length(); i++) {
                    JSONObject hourObj = hourArray.getJSONObject(i);
                    String time = hourObj.getString("time");
                    String temper = hourObj.getString("temp_c");
                    String img = hourObj.getJSONObject("time").getString("icon");
                    String wind = hourObj.getString("wind_kph");
                    weatherTimeModelArrayList.add(new WeatherTimeModel(time, temper, img, wind));
                }

                weatherTimeAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(MainActivity.this, "Please enter valid city name...", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }
}