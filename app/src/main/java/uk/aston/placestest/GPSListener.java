package uk.aston.placestest;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

//This class listens to the device location and notifies when the device location has changed
//currently class looks the same but with different variable names
//need to find different way of doing the same thing
public class GPSListener implements LocationListener {
    ArrayList<Location> locations;
    boolean isTracked;

    public GPSListener() {
        createJourney();
        isTracked = false;
    }

    public float getCurrentDistance() {
        if(locations.size() <= 1) {
            return 0;
        }

        Location firstLocation = locations.get(0);
        Location lastLocation = locations.get(locations.size() - 1);


        //divide by 1000 as we want the distance between the two points in km
        // use location.distanceTo(otherLocation) to get a distance between two locations
        return firstLocation.distanceTo(lastLocation) / 1000;

    }

    public void createJourney() {
        locations = new ArrayList<Location>();
    }


    @Override
    public void onLocationChanged(Location location) {
        if(isTracked)
        {
            //Allows us to now work out other running data
            locations.add(location);
        }
    }

}