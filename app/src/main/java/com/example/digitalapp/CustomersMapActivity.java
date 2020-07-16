package com.example.digitalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomersMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private DrawerLayout drawerLayout;

    private GoogleMap mMap;

    Location mLocation;

    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mfusedLocationClient;

    private Button request, logout, settings, destinationBtn, history;

    private LatLng pickupLocation, destinationLatLng;

    private Object GeoFire;

    private Boolean requestBol = false;

    private SupportMapFragment mapFragment;

    Marker pickupMarker, driverMarker;

    private String destination, requestService;

    private LinearLayout mDriverInfo;

    private ImageView mDriverProfileImage;

    private TextView mDriverName, mDriverPhone, mDriverCar;

    private RatingBar ratingBar;

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_map);

        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.customerMap);

        assert mapFragment != null;

        mapFragment.getMapAsync(this);

        destinationLatLng = new LatLng(-1.286267,36.884105);

        mDriverInfo = (LinearLayout) findViewById(R.id.driverInfo);

        mDriverProfileImage = (ImageView) findViewById(R.id.driverProfileImage);

        mDriverName = (TextView) findViewById(R.id.driverName);

        mDriverPhone = (TextView) findViewById(R.id.driverPhone);

        mDriverCar = (TextView) findViewById(R.id.driverCar);

        request = (Button) findViewById(R.id.request);

        settings = (Button) findViewById(R.id.settings);

        history = (Button) findViewById(R.id.history);

        logout = (Button) findViewById(R.id.logoutbtn);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mRadioGroup.check(R.id.miniVan);


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestBol) {
                    Toast.makeText(CustomersMapActivity.this, "Ride ended", Toast.LENGTH_SHORT).show();
                    endRide();

                } else {
                    int selectId = mRadioGroup.getCheckedRadioButtonId();

                    final RadioButton radioButton = (RadioButton) findViewById(selectId);

                    if (radioButton.getText() == null){
                        return;
                    }

                    requestService = radioButton.getText().toString();
                    requestBol = true;
                    String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(UserId, new GeoLocation(mLocation.getLongitude(), mLocation.getLatitude()));

                    pickupLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup here"));

//                    request.setText("Getting your rider...");

                    getClosestDriver();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomersMapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersMapActivity.this, CustomerSettingsActivity.class);
                startActivity(intent);
                return;
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersMapActivity.this, HistoryActivity.class);
                intent.putExtra("customerOrDriver", "Customer");
                startActivity(intent);
                return;
            }
        });

        //initialize places
        Places.initialize(getApplicationContext(), "AIzaSyCyiE8hXs201zEGT2NLhROP6WGdHi1EpcM");
        //Create a new places client instance
        PlacesClient placesClient = Places.createClient(this);

        //Initialize the autocomplete support fragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

//        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
//                new LatLng(0.0236, 37.9062),
//                new LatLng(0.1769, 37.9083)));
//        autocompleteFragment.setCountries("IN");
        //specify the type of place data to return
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destination = place.getName().toString();
                destinationLatLng = place.getLatLng();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocationPermission();
            }
        }

        mfusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CustomersMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(CustomersMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    if (!getDriversAroundStarted)
                        getDriversAround();
                }
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mfusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    boolean getDriversAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();

    private void getDriversAround() {
        getDriversAroundStarted = true;
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLocation.getLongitude(), mLocation.getLatitude()), 999999999);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for (Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key))
                        return;
                }

                LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title(key));
                mDriverMarker.setTag(key);

                markers.add(mDriverMarker);


            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    //get closest driver
    private double radius = 1;
    private boolean driverFound = false;
    private String driverFoundID;
    GeoQuery geoQuery;

    private void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);

        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol)
                {
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound){
                                    return;
                                }
//                                if (driverMap.get("service").equals(requestService)){
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();


                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("customerRideId", customerId);
                                    map.put("destination", destination);
                                    map.put("destinationLat", destinationLatLng.latitude);
                                    map.put("destinationLng", destinationLatLng.longitude);
                                    driverRef.updateChildren(map);

                                    getDriverLocation();
                                    getDriverInfo();
                                    getHasRideEnded();
                                    request.setText("Looking for the driver location");
//                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!driverFound)
                {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    //get driver location
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    request.setText("Driver Found");
                    if (map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if (mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
                    if (distance<100){
                        request.setText("Driver is here!");
                    }else {
                        request.setText("Driver Found: " + String.valueOf(distance));
                    }

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //get driver information
    private void getDriverInfo(){
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name")!=null){
                        mDriverName.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null){
                        mDriverPhone.setText(map.get("phone").toString());
                    }
                    if (map.get("car")!=null){
                        mDriverCar.setText(map.get("car").toString());
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue()!=null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mDriverProfileImage);
                    }

                    int ratingSum = 0;
                    float ratingTotal = 0;
                    float ratingAverage = 0;

                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingTotal++;
                    }
                    if (ratingTotal != 0 ){
                        ratingAverage = ratingSum/ratingTotal;
                        ratingBar.setRating(ratingAverage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //get information for end of ride
    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded() {
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                }else {
                    endRide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    // end ride function

    private void endRide() {
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driverLocationRefListener);

        if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;
        }

        driverFound = false;
        radius = 1;

        String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(UserId);

        if (pickupMarker != null){
            pickupMarker.remove();
        }
        if (driverMarker != null){
            driverMarker.remove();
        }
        request.setText("Call Driver");

        mDriverName.setText("");
        mDriverInfo.setVisibility(View.GONE);
        mDriverPhone.setText("");
        mDriverCar.setText("Destination: --");
        mDriverProfileImage.setImageResource(R.drawable.ic_default_user);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
