package uk.aston.placestest;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

//This class provides access to the system location services which allow applications to obtain periodic updates of the device's geographical location
//
public class GPSService extends Service {
    private long beginningTime = 0;
    private long finishTime = 0;

    final int time = 2;
    final int dist = 2;

    private final int notificationId = 001;

    private GPSListener gpsListener;
    private LocationManager manager;
    private final IBinder binder = new LocationServiceBinder();

    //created after record button is pressed
    @Override
    public void onCreate() {
        super.onCreate();

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsListener = new GPSListener();
        gpsListener.isTracked = false;


        //if statement conducts a permission check before the requestLocationUpdates method call to see if the user has given us permission to use their location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        manager.requestLocationUpdates(manager.GPS_PROVIDER, time, dist, gpsListener);
    }






    @Override
    public void onDestroy() {
        super.onDestroy();

        manager.removeUpdates(gpsListener);
        gpsListener = null;
        manager = null;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //cancel notification services
        notificationManager.cancel(notificationId);


    }



    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    protected float getDistance() {
        return gpsListener.getCurrentDistance();
    }

    protected void playJourney() {
        gpsListener.isTracked = true;
        gpsListener.createJourney();
        beginningTime = SystemClock.elapsedRealtime();
        finishTime = 0;
    }

    /* Get the duration of the current journey */
    protected double getDuration() {
        if(beginningTime == 0) {
            return 0.0;
        }

        long endTime = SystemClock.elapsedRealtime();

        if(this.finishTime != 0) {
            // saveJourney has been called, until playJourney is called again display constant time
            endTime = this.finishTime;
        }

        long elapsedMilliSeconds = endTime - beginningTime;
        String ep = String.valueOf(elapsedMilliSeconds);
        Log.d("msg", ep);
        return elapsedMilliSeconds / 1000.0;
    }

    //If start time is currently not equal to 0, that means location is being tracked as the user is running
    //Thus: the return value is true

    protected boolean recordingLocation()
    {
        if (beginningTime != 0)
        {
            return true;
        } else {
            return false;
        }
    }

    public class LocationServiceBinder extends Binder {
        // would like to get the distance in km for activity
        // the activity will keep track of the duration using chronometer
        public float getDistance() {
            return GPSService.this.getDistance();
        }

        //returns our service methods
        public double getTime() {
            // get duration in seconds
            return GPSService.this.getDuration();
        }

        //
        public boolean recordingLocation() {return GPSService.this.recordingLocation();}

        public void playJourney() {
            GPSService.this.playJourney();
        }



        public void finishJourney(){

            //Change layout of code so it does not resemble the copied part
            gpsListener.isTracked = false;
            finishTime = SystemClock.elapsedRealtime();
            beginningTime = 0;
            gpsListener.createJourney();

        }

    }
}
