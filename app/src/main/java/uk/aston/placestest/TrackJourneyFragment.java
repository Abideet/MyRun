package uk.aston.placestest;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import uk.aston.placestest.Database.Journey;
import uk.aston.placestest.Database.JourneyViewModel;
import uk.aston.placestest.Database.MyRoomDatabase;
import uk.aston.placestest.weather.WeatherActivity;


//could extract the methods into an interface and just leave UI functions here

//Change fragment
public class TrackJourneyFragment extends Fragment implements View.OnTouchListener, GestureDetector.OnGestureListener  {


    //Variables that handle the gesture
    //Can access the devices sensors through this object
    private SensorManager sensorManager;
    private float acelVal; //Current acceleration value and gravity
    private float acelLast; //Last acceleration value and gravity
    private float shake; //Acceleration value differ from gravity

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    public static final int NEW_JOURNEY_ACTIVITY_REQUEST_CODE = 1;

    public static final String EXTRA_REPLY = "com.example.android.roomwordsample.REPLY";

    private GPSService.LocationServiceBinder GPSService;

    private LocationListener locListener;

    private LocationManager manager;

    private Button endButton;
    private Button startButton;


    public TextView tVelocity;
    public TextView tDistance;
    public TextView tTime;

    public TextView temp;

    String time;
    String dist;
    String avgs;


    JourneyViewModel journeyViewModel;



    //Interacts with the service by handling the processing and sending of the running data back to GPS service
    private Handler handler = new Handler();

    //TODO Change copied thread from application
    private ServiceConnection gpsService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            GPSService = (GPSService.LocationServiceBinder) iBinder;

            // if currently tracking then enable stopButton and disable startButton
            configButtons();

            //This activity should always be running in the background so create a new thread so that the user can interact with the main UI thread while this is going on

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (GPSService != null) {

                        float d = (float) GPSService.getTime();
                        long duration = (long) d;
                        double distance = GPSService.getDistance();

                        long h = duration / 3600;
                        long m = (duration % 3600) / 60;
                        long s = duration % 60;

                        double speed = 0;
                        if (d != 0) {
                            speed = distance / (d / 3600);
                        }

                        time = String.format("%02d:%02d:%02d", h, m, s);
                        dist = String.format("%.2f KM", distance);
                        avgs = String.format("%.2f KM/H", speed);

                        handler.post(new Runnable()
                        {
                            @Override
                            public void run() {
                                tTime.setText(time);
                                tVelocity.setText(avgs);
                                tDistance.setText(dist);
                            }
                        });

                        try {
                            Thread.sleep(600);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            GPSService = null;
        }
    };



    // whenever activity is reloaded while still tracking a journey (if back button is clicked for example)
    private void configButtons() {

        // Permissions are not enabled so buttons should also not be enabled
        boolean b = GPSService.recordingLocation();

        if (b) {
            endButton.setEnabled(true);
            startButton.setEnabled(false);
        } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startButton.setEnabled(false);
            endButton.setEnabled(false);
            //only condition left is that
        } else {
            endButton.setEnabled(false);
            startButton.setEnabled(true);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View v = inflater.inflate(R.layout.fragment_track_journey, container, false);

        locListener = new GPSListener();
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        tDistance = v.findViewById(R.id.distanceText);
        tTime = v.findViewById(R.id.durationText);
        tVelocity = v.findViewById(R.id.avgSpeedText);

        startButton = v.findViewById(R.id.startButton);
        endButton = v.findViewById(R.id.stopButton);

        temp = v.findViewById(R.id.temp);
        temp.setVisibility(View.INVISIBLE);

        endButton.setEnabled(false);
        startButton.setEnabled(false);


        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        //mGestureDetector = new GestureDetector(getActivity(), getActivity());





        getActivity().startService(new Intent(getActivity(), GPSService.class));
        //the activity binds to the LocationBoundService by calling this method which will create a long standing connection between them
        //Invokes the onBind method when this activity wants to bind with the service
        getActivity().bindService(
                new Intent(getActivity(), GPSService.class), gpsService, Context.BIND_AUTO_CREATE);

        return v;
    }


    private final SensorEventListener sensorListener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent)
        {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = acelVal - acelLast;
            shake = shake * 0.9f + delta;

            //phone will be shaking anyways during a run -> high shake threshold means that the standard shake is not implemented
            if(shake > 12)
            {
                Log.d("shake", "Shake Worked");
                //TODO: Bug - duplicates runs
                runStart();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i)
        {


        }
    };


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Uses getDatabase method so there is only ever one instance of the database





        //Buttons for this fragment
        //TODO: recording distance and other values is not working
        Button stop = requireActivity().findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runStopped();

            }
        });



        Button start = requireActivity().findViewById(R.id.startButton);
        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                runStart();



            }
        });

        ImageButton weather = requireActivity().findViewById(R.id.weatherButton);
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                temp.setVisibility(View.VISIBLE);
//
//                WeatherActivity w = new WeatherActivity();
//
//                temp.setText(w.getTemperature());





                //NavHostFragment.findNavController(TrackJourneyFragment.this).navigate(R.id.action_trackJourneyFragment_to_weatherFragment);

                //take user to previous activity
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                startActivity(intent);

            }
        });
    }

    private void runStart()
    {
        GPSService.playJourney();
        startButton.setEnabled(false);
        endButton.setEnabled(true);

    }



    private void runStopped()
    {
        final MyRoomDatabase db = MyRoomDatabase.getDatabase(getActivity());


                double distance = GPSService.getDistance();

                GPSService.finishJourney();

                startButton.setEnabled(false);
                endButton.setEnabled(false);


                //Inserting Run Data into our Room Database
                Journey journey = new Journey();

                //Get and parse time
                TextView textTime = tTime;
                String stringTime = textTime.getText().toString();


                List<String> timeList = new ArrayList<String>();

                BigInteger duration = new BigInteger(String.valueOf(convertTimeStringtoInteger(stringTime)));



                Log.d(TAG, duration.toString());
                journey.setMduration(duration);


                TextView textDistance = tDistance;
                String stringDistance = textDistance.getText().toString();


                //distance = Float.parseFloat(stringDistance);
                distance =  convertDistanceStringtoFloat(stringDistance);


                journey.setMdistance(distance);


                TextView textSpeed = tVelocity;
                String stringSpeed = textSpeed.getText().toString();
                double speed = convertSpeedStringtoDouble(stringSpeed);

                journey.setMSpeed(speed);

                String stringName = "Enter Name";

                journey.setmName(stringName);

                try
                {
                    db.journeyDao().insert(journey);
                    Log.d(TAG,"insertion worked");
                }catch(SQLiteConstraintException e){
                    Log.d(TAG, "insertion failed");
                }

                Intent intent = new Intent(getActivity(), ViewJourneyActivity.class);
                startActivity(intent);

    }


    public BigInteger convertTimeStringtoInteger(String str){
        List<String> tokens = new ArrayList<>();

        StringTokenizer tokenizer = new StringTokenizer(str, ":");
        int hour =Integer.parseInt(tokenizer.nextToken());
        int minute= Integer.parseInt(tokenizer.nextToken());
        int seconds = Integer.parseInt(tokenizer.nextToken());

        Integer secondsInt = hour * 3600 + minute * 60 + seconds;

        BigInteger secondsBigInt = BigInteger.valueOf(secondsInt);


        return secondsBigInt;
    }

    public Float convertDistanceStringtoFloat(String str)
    {
        List<String> tokens = new ArrayList<>();

        StringTokenizer tokenizer = new StringTokenizer(str, "KM");
        Float distance = Float.parseFloat(tokenizer.nextToken());

        Float distanceBig = Float.valueOf(distance);


        return distanceBig;
    }


    public Double convertSpeedStringtoDouble(String str)
    {

        List<String> tokens = new ArrayList<>();

        StringTokenizer tokenizer = new StringTokenizer(str, "KM/H");
        Double distance = Double.parseDouble(tokenizer.nextToken());

        Double distanceBig = Double.valueOf(distance);


        return distanceBig;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gpsService != null) {
            getActivity().unbindService(gpsService);
            gpsService = null;
        }
    }



    //using the onRequestPermissionsResult to manage the availiability of the buttons and
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    // configure the buttons if permission granted
                    configButtons();


                    //my version - change to old if this causes problems
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    )
                    {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions

                        return;
                    }

                    manager.requestLocationUpdates(manager.GPS_PROVIDER, 3, 3, locListener);
                    //manager.requestLocationUpdates(manager.GPS_PROVIDER, 3, 3, locListener);
                    //}

                }


                return;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return true;
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d(TAG, "onFling works");
        return false;
    }
}
