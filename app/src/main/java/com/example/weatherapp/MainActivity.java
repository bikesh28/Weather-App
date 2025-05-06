package com.example.weatherapp;

import static android.Manifest.*;

import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.*;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText cityInput;
    private Button searchButton;
    private TextView temperatureText;
    private TextView descriptionText;
    private TextView cityName;
    ImageView weatherImg;
    private GoogleMap map;
    private FusedLocationProviderClient locationClient;

    private RequestQueue requestQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        cityInput = findViewById(R.id.cityInput);
        searchButton = findViewById(R.id.searchButton);
        temperatureText = findViewById(R.id.temperatureText);
        descriptionText = findViewById(R.id.description_text);
        cityName = findViewById(R.id.cityName);
        weatherImg = findViewById(R.id.weatherImg);


        //set request Queue
        requestQueue = Volley.newRequestQueue(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchButton.setOnClickListener(v ->{
            Log.d("click","hello");
            String city = cityInput.getText().toString().trim();
            Log.d( "api: ",city);
            if(!city.isEmpty()){
                fetchWeatherData(city);

            }else{
                Toast.makeText(MainActivity.this,"Enter City",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWeatherData(String city){

        String API_KEY = "fd8127b71f05c5fbc057b39c9e4ec6dc";
        String url ="https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid="+ API_KEY +"&units=metric";

        JsonObjectRequest request =  new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("api",response.toString());
                        try {
                            JSONObject main = response.getJSONObject("main");
                            JSONObject weather = response.getJSONArray("weather").getJSONObject(0);
                            JSONObject coordinate = response.getJSONObject(("coord"));
                            double latitude = coordinate.getDouble("lat");
                            double longitude = coordinate.getDouble("lon");
                            LatLng latlng = new LatLng(latitude, longitude);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));

                            double temperature = main.getDouble("temp");
                            String descriptioin = weather.getString("description");
                            String city = response.getString("name");

                            temperatureText.setText(String.format("%.1f°C",temperature) );
                            descriptionText.setText(descriptioin);
                            cityName.setText(city);

                            String iconUrl = "https://openweathermap.org/img/wn/" + weather.getString("icon") +"@2x.png";
                            Glide.with(MainActivity.this).load(iconUrl).into(weatherImg);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("error: ", error.toString());

                    }
                });
        requestQueue.add(request);
    }
    private void fetchWeatherDataUsingLatLng(LatLng coordinate){

        String API_KEY = "fd8127b71f05c5fbc057b39c9e4ec6dc";
        String url ="https://api.openweathermap.org/data/2.5/weather?lat=" + coordinate.latitude + "&lon="+ coordinate.longitude + "&appid="+ API_KEY +"&units=metric";


        JsonObjectRequest request =  new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("api",response.toString());
                        try {
                            JSONObject main = response.getJSONObject("main");
                            JSONObject weather = response.getJSONArray("weather").getJSONObject(0);


                            double temperature = main.getDouble("temp");
                            String descriptioin = weather.getString("description");
                            String city = response.getString("name");

                            temperatureText.setText(String.format("%.1f°C",temperature) );
                            descriptionText.setText(descriptioin);
                            cityName.setText(city);

                            String iconUrl = "https://openweathermap.org/img/wn/" + weather.getString("icon") +"@2x.png";
                            Glide.with(MainActivity.this).load(iconUrl).into(weatherImg);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("error: ", error.toString());

                    }
                });
        requestQueue.add(request);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            getCurrentLocaton();
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1

            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if ( grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocaton();

            }
        }
    }
    private void getCurrentLocaton(){
        if(ContextCompat.checkSelfPermission(this,permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(this , location -> {
                    if (location != null){
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
                        fetchWeatherDataUsingLatLng(currentLocation);
                    }
                });
    }

}
