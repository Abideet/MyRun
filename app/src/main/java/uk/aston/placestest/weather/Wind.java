package uk.aston.placestest.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    @Expose
    private Double windSpeed;




    @SerializedName("deg")
    @Expose
    private int direction;






    public Double getWindSpeed() {
        return windSpeed;
    }

    public int getWindDirection() {
        return direction;
    }


    public class Rain
    {

        @SerializedName("1h")
        @Expose
        private Double rainLast1h;




        public Double getRainLast1h() {
            return rainLast1h;
        }

    }







}
