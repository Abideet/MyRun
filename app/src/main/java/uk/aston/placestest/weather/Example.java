package uk.aston.placestest.weather;

import com.google.gson.annotations.SerializedName;

public class Example {
   @SerializedName("main")
    Main main;

    public Main getMain() {
        return main;
    }

}
