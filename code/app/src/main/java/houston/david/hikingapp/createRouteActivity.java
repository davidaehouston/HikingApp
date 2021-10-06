package houston.david.hikingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import data.PointType;
import data.Point;
import util.LocationHelper;
import util.Util;

/**
 * This class is responsible for creating a route, following a users location
 * and setting waypoints.
 */
public class createRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_FINE_LOCATIONS = 99;
    public static final int locationRequestCode = 1000;
    public static final List<LatLng> userLocList = new ArrayList<>();
    public static final List<Point> userpoints = new ArrayList<>();
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private MapView mapView;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Button finishRecRoute, waypointRecRoute, startRecRoute;
    private ToggleButton pauseRouteToggle;
    private PolylineOptions options;
    private Polyline polyline;
    private TextView latDisp, lonDisp, timerDisp, statusDisp, timerstatus;
    private FloatingActionButton fmb;
    private LocationHelper locationHelper;
    private Timer routeTimer;
    private TimerTask routeTimerTask;
    private double routeTime = 0.0;
    long time = 5000; //5 second
    private LatLng currLoc;
    private Util utility;


    /**
     * Starter class for this acitvity, assigning ids and on click
     * listneers as well as initalising the location request.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        //Assign variables to views in XML.
        startRecRoute = findViewById(R.id.startrecrouteBtn);
        pauseRouteToggle = (ToggleButton) findViewById(R.id.pauserecrouteBtn);
        finishRecRoute = findViewById(R.id.finishrecrouteBtn);
        waypointRecRoute = findViewById(R.id.waypointrecrouteBtn);
        mapView = findViewById(R.id.mapView03);
        latDisp = findViewById(R.id.latitudedisp);
        lonDisp = findViewById(R.id.longitudedisp);
        statusDisp = findViewById(R.id.tv_RecordingStatusID);
        timerDisp = findViewById(R.id.tv_RecordingStatusID);
        fmb = findViewById(R.id.fab_MapStyleBtn);
        timerstatus = findViewById(R.id.tv_timertext);
        statusDisp.setText(R.string.defaultRecordingStatus);
        utility = new Util();
        locationHelper= new LocationHelper();

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);

        //Initialise the fusedlocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(createRouteActivity.this);

        //Create the location request
        createLocationRequest();


        pauseRouteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onPause();
                    statusDisp.setText(R.string.PAUSE_RECORD);
                    statusDisp.setTextColor(Color.YELLOW);
                    utility.pauseTimer();
                } else {
                    onResume();
                    statusDisp.setText(R.string.RECORDING_ROUTE);
                    statusDisp.setTextColor(Color.GREEN);
                    routeTimer = new Timer();

                }
            }
        });

        fmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationHelper.changeMapType(v, mMap);
            }
        });


    }

    /**
     * Repsonsible to building the location request, determining
     * the intervals and their priority.
     */
    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * This method takes a list of type point, will draw the polyline based on the LatLng
     * of each point while also using the POINTTYPE to assign a markers based on their value.
     *
     * @param points
     */
    public void drawOnMap(List<Point> points) {

        List<LatLng> pointLatLngList = new ArrayList<>();

        MarkerOptions starterMark = new MarkerOptions();
        MarkerOptions endMark = new MarkerOptions();
        MarkerOptions waypointMark = new MarkerOptions();

        starterMark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        waypointMark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));


        for (Point p : points) {
            LatLng LatLngPoints = new LatLng(p.getLatitude(), p.getLongitude());
            pointLatLngList.add(LatLngPoints);

            if(polyline == null) {
                options = new PolylineOptions()
                        .color(Color.WHITE)
                        .width(5)
                        .geodesic(true);
                options.addAll(pointLatLngList);
                mMap.addPolyline(options);
            } else {
                polyline.setPoints(pointLatLngList);
            }

            //Check the point type of each point in the list and assin a marker based on their type.
            if (p.getPointType() == PointType.START_POINT) {
                options.startCap(new RoundCap());
                LatLng markerLatLng = new LatLng(p.getLatitude(), p.getLongitude());
                starterMark.position(markerLatLng);
                mMap.addMarker(starterMark);
            } else if (p.getPointType() == PointType.ENDPOINT) {
                options.endCap(new RoundCap());
                LatLng markerLatLng = new LatLng(p.getLatitude(), p.getLongitude());
                endMark.position(markerLatLng);
                mMap.addMarker(endMark);
            } else if (p.getPointType() == PointType.WAYPOINT) {
                LatLng markerLatLng = new LatLng(p.getLatitude(), p.getLongitude());
                waypointMark.position(markerLatLng);
                mMap.addMarker(waypointMark);
            }
        }

    }

    /**
     * This metohd starts the location callback that is responsible for creating a new point
     * each time and then adding to a list of type 'Point'
     * It also allows for live updates on current location such as Latitude and
     * Longitude and altitude.
     */
    public void startLocationCallBack() {

        // initialise the callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //if there is no location result return
                if (locationResult == null) {
                    return;
                }
                //For each loop to go through each new location
                for (Location location : locationResult.getLocations()) {
                    //Display the current Latitude and Longitude.
                    latDisp.setText(String.valueOf(location.getLatitude()));
                    lonDisp.setText(String.valueOf(location.getLongitude()));
                    currLoc = new LatLng(location.getLatitude(), location.getLongitude());
                    zoomToUserLocation(currLoc);
                    //Create a new Point with the current latitude and Longitude, standard point type and description.
                    Point p = new Point(location.getLatitude(), location.getLongitude(), "", PointType.STANDARD_POINT, Point.URI_DEFAULT);
                    //Assign a point type.
                    AssignPointType(p);
                    //Add to userpoints List at the top of the class.
                    userpoints.add(p);
                    //Waypoint button onclick this will take the very last point and put it into and intent
                    waypointRecRoute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //create new intent
                            Intent i = new Intent(createRouteActivity.this, CreateWayPointActivity.class);
                            //create a new bundle for the selected point.
                            Bundle bundle = new Bundle();
                            //Add point to the bundle
                            bundle.putSerializable("point", p);
                            i.putExtras(bundle);
                            startActivity(i);
                            stopLocUpdates();
                        }
                    });

                    //create a handler & runnable for for continuous polyline on map when user presses start.
                    Handler h = new Handler(Looper.getMainLooper());
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {

                            drawOnMap(userpoints);
                            h.postDelayed(this, time);
                        }
                    };

                    startRecRoute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            h.postDelayed(r, 0);
                            userpoints.clear();
                            statusDisp.setText("RECORDING ROUTE");
                            statusDisp.setTextColor(Color.GREEN);
                            routeTimer = new Timer();
                            utility.startTimer(timerstatus);
                            startRecRoute.setEnabled(false);

                        }

                    });

                    finishRecRoute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (h != null && r != null) {
                                utility.pauseTimer();
                                h.removeCallbacks(r);
                            }

                            p.setPointType(PointType.ENDPOINT);

                            Intent finishIntent = new Intent(createRouteActivity.this, RouteCreationOverviewActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("routePoints", (Serializable) userpoints);
                            finishIntent.putExtras(bundle);
                            startActivity(finishIntent);
                            onStop();


                        }
                    });

                }
            }

        };

    }

    /**
     * Assigns point types to each point depending on the users actions.
     * @param p
     */
    private void AssignPointType(Point p) {
        if (userpoints.isEmpty()) {
            p.setPointType(PointType.START_POINT);
        } else if (p.getPointType() == PointType.ENDPOINT) {
            p.setPointType(PointType.ENDPOINT);
        } else if (p.getPointType() == PointType.WAYPOINT) {
            p.setPointType(PointType.WAYPOINT);
        } else {
            p.setPointType(PointType.STANDARD_POINT);
        }
    }


    /**
     * Method called when activity starts,
     * checks location permissions and starts if available.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //call this method
            checkSettingsStartLocUpdates();
            startLocationCallBack();
        } else {
            checkDevicePermission();
        }
    }

    /**
     * This method will stop location updates and pause the timer if started.
     */
    @Override
    protected void onStop() {
        super.onStop();
        stopLocUpdates();
        utility.pauseTimer();

    }

    /**
     * when called, this overridden method
     * Will stop location updates and pause the timer.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopLocUpdates();
        utility.pauseTimer();
    }

    /**
     * when called this method will start location updates.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startLocUpdates();
        if (routeTimer == null) {
            return;
        } else {
            utility.startTimer(timerstatus);
        }
    }


    /**
     * Check device permissions
     * creates a setting request and client to determine
     * if location settings are granted.
     */
    private void checkSettingsStartLocUpdates() {

        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build();

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Settings match, location updates in progress
                startLocUpdates();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(createRouteActivity.this, 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * Checks permissions each time before starting location updates
     * via the fusedLocationProviderClient.
     */
    private void startLocUpdates() {
        checkDevicePermission();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * when called this method will remove all updates from
     * the furedLocationProviderClient.
     */
    private void stopLocUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * THis method is called when the map is loaded into the activity
     * location can be enabled and other UI settings.
     * @param googleMap
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        checkDevicePermission();
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setPadding(25, 200, 0, 375);


    }

    /**
     * Method to check device permissions
     */
    public void checkDevicePermission() {

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request for permission

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                //access to location
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                }

            });
        }

    }

    /**
     * This method will check for permissions granted by the user.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_FINE_LOCATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingsStartLocUpdates();
            } else {
                //need permissions - to be completed.
            }
        }
    }


    /**
     * Takes a LatLng as a parameter and zooms the camera on the map to that position
     */
    private void zoomToUserLocation(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

    }
}