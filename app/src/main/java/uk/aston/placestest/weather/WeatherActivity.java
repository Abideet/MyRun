package uk.aston.placestest.weather;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.aston.placestest.R;

//TODO: - Use map lat & long data to find weather in one click


public class WeatherActivity extends AppCompatActivity {


    TextView viewTemp;
    TextView viewPressure;
    TextView viewHumidity;

    TextView viewTempMax;
    TextView viewTempMin;

    TextView viewWindSpeed;

    TextView viewRainVol;


    String apikey = "fcd2c236893559071f53147e2c72132f";

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    String latitude, longitude;

    JSONObject googlePlaceJSON;


    public Integer getTemperature()
    {
        return temperature;
    }

    Integer temperature;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //get user location data from lat/lng in parks feature rather than them having to enter the city name

        viewTemp = findViewById(R.id.tempView);
        viewPressure = findViewById(R.id.pressureView);
        viewHumidity = findViewById(R.id.humidityView);

//        viewTempMax = findViewById(R.id.tempMaxView);
//        viewTempMin = findViewById(R.id.tempMinView);

        viewWindSpeed = findViewById(R.id.windSpeedView);

        viewRainVol = findViewById(R.id.rain1hView);


        //code to trigger button lat display
        Button button = findViewById(R.id.weatherButton);

        //getLocation();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
            }
        });

    }

    //could cut down - could get lat and long in maybe a few lines of code
//    private void getLocation() {
//
//        //Try to integrate this code from
//        if (ActivityCompat.checkSelfPermission(
//                WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                WeatherActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
//        } else {
//
//            //
//            //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (locationGPS != null) {
//                double lat = locationGPS.getLatitude();
//                double longi = locationGPS.getLongitude();
//                latitude = String.valueOf(lat);
//                longitude = String.valueOf(longi);
//
//                viewTemp.setText(longitude+"C");
//
//            } else {
//                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void getLocation()
    {
        if (ActivityCompat.checkSelfPermission(
                WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                WeatherActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double lng = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(lng);

                //call get weather method after getting lat & lng
                getWeather(lat, lng);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //change this method to include latitude and long
    //gets the weather data from openWeatherMap API using city name
//    public void getWeather(View v) throws IOException {
//        //"https://api.openweathermap.org/data/2.5/"
//
//
//        Retrofit rBuild = new Retrofit.Builder()
//                .baseUrl("https://api.openweathermap.org/data/2.5/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        ApiInterface myapi=rBuild .create(ApiInterface.class);
//
//
//        Call<Example> examplecall=myapi.getTemperature(inputCity.getText().toString().trim(),apikey);
//
//        Parser parser = new Parser();
//
//        //New call useing lat and lng
//        //Call<Example> examplecall=myapi.getTemperature(latitude, longitude,apikey);
//
//
//        examplecall.enqueue(new Callback<Example>()
//        {
//            @Override
//            public void onResponse(Call<Example> call, Response<Example> response)
//            {
//                if(response.code()==404)
//                {
//                    Toast.makeText(WeatherActivity.this,"Please Enter a valid City",Toast.LENGTH_LONG).show();
//                }
//                else if(!(response.isSuccessful()))
//                {
//                    Toast.makeText(WeatherActivity.this,response.code()+" ",Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                //Parse and Store response
//                Example mydata=response.body();
//                Main main=mydata.getMain();
//                Double temp=main.getTemp();
//                //convert from fahrenheit to degrees
//                temperature=(int)(temp-273.15);
//
//                //Set the temperature response data as the text inside the text view
//                viewTemp.setText(String.valueOf(temperature)+"C");
//
//            }
//
//            @Override
//            public void onFailure(Call<Example> call, Throwable t)
//            {
//                Toast.makeText(WeatherActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    public void getWeather(double latitude, double longitude) {
        Retrofit rBuild = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface myapi = rBuild.create(ApiInterface.class);

        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);

        //this api call is changed to use lat lng instead of cityName
        Call<Example> examplecall = myapi.getTemperature(lat, lng, apikey);



        examplecall.enqueue(new Callback<Example>()
        {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if (response.code() == 404) {
                    Toast.makeText(WeatherActivity.this, "Unable to get weather data for this location", Toast.LENGTH_LONG).show();
                } else if (!(response.isSuccessful())) {
                    Toast.makeText(WeatherActivity.this, response.code() + " ", Toast.LENGTH_LONG).show();
                    return;
                }

                //Parse and Store response
                Example mydata = response.body();

                //Main Temp Response
                Main main = mydata.getMain();
                Double temp = main.getTemp();

                Integer pressure = main.getPressure();
                Integer humidity = main.getHumidity();

                Double tempMin = main.getTempMin();
                Double tempMax = main.getTempMax();


                //convert from fahrenheit to degrees
                temperature = (int) (temp - 273.15);

                tempMax = (double) (tempMax - 273.15);

                tempMin = (double) (tempMin - 272.15);


                //Set the temperature response data as the text inside the text view
                viewTemp.setText(String.valueOf(temperature) + "°C");

                viewPressure.setText(String.valueOf(pressure) + " hPa");
                viewHumidity.setText(String.valueOf(humidity) + "%");

                //Wind Response
                Wind wind = mydata.getWind();

                Double windSpeed = wind.getWindSpeed();

                //this direction is stored in degrees -> need to convert to directional data
                int windDirec = wind.getWindDirection();

                String[] compassPoints = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
                //calculate wind direction in terms of compass points by dividing the wind direction in degrees by 22.5 and rounding to the nearest integer
                int index = Math.round(windDirec / 22.5f); //gives us a number between 0 and 15 which corresponds to the 16 compass points (N, NE, NNE, etc)
                //Using modulus, find the corresponding compass point to the index value
                String direction = compassPoints[index % 16];


                viewWindSpeed.setText(windSpeed + "m/s " + direction);

                //Rain Response
                Wind.Rain rain = mydata.getRain();

                //This invocation seems to cause the code to break
                //Double rainVol = rain.getRainLast1h();
//
                //viewRainVol.setText(String.valueOf(rainVol) + "mm");




//                viewTempMax.setText(String.valueOf(tempMax) + "C");
//                viewTempMin.setText(String.valueOf(tempMin) + "C");
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Toast.makeText(WeatherActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}

