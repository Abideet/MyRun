package uk.aston.placestest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


public class RunFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;
    private MapView mMapView;

    private View view;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    //Zoom level (15 for street view)
    public static final float INITIAL_ZOOM = 17f;

    private int ProximityRadius = 10000;
    private double latitude, longitude;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        view =  inflater.inflate(R.layout.fragment_run, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = view.findViewById(R.id.mainFragment);

        //if it exists
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }


        Button recordButton = view.findViewById(R.id.recordButton);

        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(RunFragment.this).navigate(R.id.action_runFragment_to_trackJourneyFragment);


            }
        });

        Button places = view.findViewById(R.id.nearbyPlacesButton);

        places.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                NavHostFragment.findNavController(RunFragment.this).navigate(R.id.action_runFragment_to_placesActivity);

            }
        });

    }



    //@Override
    public void onMapReady(GoogleMap googleMap) {
//        MainActivity m = new MapsActivity();
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng home = new LatLng(52.59245871179721, -0.27152565634457315);

        LatLng nYork = new LatLng(40.7128,74.0060);

        //Pan and zooms camera to desired location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, INITIAL_ZOOM));

        //enable location tracking
        enableMyLocation(mMap);
    }



    //Changes map type based on menu selection
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        //Change map type based on user selection
//        switch (item.getItemId()){
//            case R.id.normal_map:
//                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                return true;
//            case R.id.hybrid_map:
//                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                return true;
//            case R.id.satellite_map:
//                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                return true;
//            case R.id.terrain_map:
//                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                return true;
////            case R.id.action_progress:
////                //Intent to ProgressActivity
////                Intent progressIntent = new Intent(this,
////                        ProgressActivity.class);
////                startActivity(progressIntent);
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    //Requests the location permission from the user
    private void enableMyLocation(GoogleMap map){
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else{
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }
}
