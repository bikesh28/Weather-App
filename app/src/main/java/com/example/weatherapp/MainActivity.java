package com.example.weatherapp;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText cityInput;
    private Button searchButton;
    private TextView temperatureText;
    private TextView descriptionText;
    private TextView cityName;
    ImageView weatherImg;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        searchButton = findViewById(R.id.searchButton);
        temperatureText = findViewById(R.id.temperatureText);
        descriptionText = findViewById(R.id.description_text);
        cityName = findViewById(R.id.cityName);
        weatherImg = findViewById(R.id.weatherImg);

        //set request Queue
        requestQueue = Volley.newRequestQueue(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapCard);
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


                            double temperature = main.getDouble("temp");
                            String descriptioin = weather.getString("description");
                            String city = weather.getString("name");

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

    }
}
