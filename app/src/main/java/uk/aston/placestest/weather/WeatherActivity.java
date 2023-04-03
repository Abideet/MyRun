package uk.aston.placestest.weather;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.aston.placestest.R;

//TODO: - Use map lat & long data to find weather in one click


public class WeatherActivity extends AppCompatActivity {

    EditText inputCity;
    TextView viewTemp;
    TextView viewPressure;
    String apikey = "fcd2c236893559071f53147e2c72132f";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //get user location data from lat/lng in parks feature rather than them having to enter the city name
        inputCity = findViewById(R.id.inputCity);
        viewTemp = findViewById(R.id.tempView);

    }

    public void getWeather(View v)
    {
        Retrofit rBuild = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface myapi=rBuild .create(ApiInterface.class);

        //Send request with the city name that the user has entered
        Call<Example> examplecall=myapi.getTemperature(inputCity.getText().toString().trim(),apikey);
        examplecall.enqueue(new Callback<Example>()
        {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response)
            {
                if(response.code()==404)
                {
                    Toast.makeText(WeatherActivity.this,"Please Enter a valid City",Toast.LENGTH_LONG).show();
                }
                else if(!(response.isSuccessful()))
                {
                    Toast.makeText(WeatherActivity.this,response.code()+" ",Toast.LENGTH_LONG).show();
                    return;
                }

                //Parse and Store response
                Example mydata=response.body();
                Main main=mydata.getMain();
                Double temp=main.getTemp();
                Integer temperature=(int)(temp-273.15);

                //Set the temperature response data as the text inside the text view
                viewTemp.setText(String.valueOf(temperature)+"C");
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t)
            {
                Toast.makeText(WeatherActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}

