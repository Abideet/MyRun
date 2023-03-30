package uk.aston.placestest.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//Interface which includes a Get operation that interacts with the web API
public interface ApiInterface
{

    @GET("weather")
    Call<Example> getTemperature(@Query("q") String city,
                                 @Query("appid") String apikey);




}
