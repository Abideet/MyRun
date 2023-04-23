package uk.aston.placestest.weather;

import com.google.gson.annotations.SerializedName;

public class Example {
   @SerializedName("main")
    Main main;
    Wind wind;
    Wind.Rain rain;

    public Main getMain() {
        return main;
    }

    public Wind getWind() { return wind; }

    public Wind.Rain getRain() { return rain; }

}
