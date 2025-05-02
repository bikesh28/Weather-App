package com.example.weatherapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    private EditText cityInput;
    private Button searchButton;
    private TextView temperatureText;
    private TextView descriptionText;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        searchButton = findViewById(R.id.searchButton);
        temperatureText = findViewById(R.id.temperatureText);
        descriptionText = findViewById(R.id.description_text);

        //set request Queue
        requestQueue = Volley.newRequestQueue(this);

        searchButton.setOnClickListener(v ->{
            String city = cityInput.getText().toString().trim();
            if(!city.isEmpty()){
                fetchWeatherData(city);

            }else{
                Toast.makeText(MainActivity.this,"Enter City",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWeatherData(String city){

        String API_KEY = "fd8127b71f05c5fbc057b39c9e4ec6dc";
        String url ="https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid="+ API_KEY;


    }
}
