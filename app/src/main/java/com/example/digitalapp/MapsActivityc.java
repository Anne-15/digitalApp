package com.example.digitalapp;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;


public class MapsActivityc extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE =101;
    private GoogleMap mMap;
    private MarkerOptions place1, place2,place3,place4,place5,place6,place7,place8,place9,place10,place11,place12
            ,place13,place14,place15,place16,place17,place18,place19,place20;
    private Polyline currentPolyline;
    SearchView searchView;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    //private LatLngBounds NAIROBI = new LatLngBounds(new LatLng(), new LatLng());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activityc);
        /**Get directions*/
        place1 = new MarkerOptions().position(new LatLng(-1.2865394,36.82742)).title("Bus Station");
        place2 = new MarkerOptions().position(new LatLng(-1.2814284,36.8204588)).title("Koja");



        /**Search tool*/
        searchView = findViewById(R.id.sv_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MapsActivityc.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(3.0f);
        mMap.setMaxZoomPreference(20.0f);

        Log.d("mylog", "added markers");
        mMap.addMarker(place1).isFlat();
        mMap.addMarker(place2).isFlat();
    }

}
