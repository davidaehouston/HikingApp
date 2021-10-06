package houston.david.hikingapp;


import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.text.LineBreaker;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import data.*;
import io.grpc.internal.AbstractReadableBuffer;
import util.GeoFenceHelper;
import util.LocationHelper;
import util.Util;

/**
 * This class is responsible for displaying the route to the user, information such as geofencing
 * distance tavlled and all UI based activity lives here.
 */
public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * Private Broadcast Reciever, this can grab information about the geofence that is being triggered
     * requires further implmentation.
     * Do not use.
     */
    private BroadcastReceiver geofenceReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent geointent = getIntent();
            double intentLat = intent.getDoubleExtra("lat", 123.0);
            double intentLon = intent.getDoubleExtra("lon", 123.0);
            String id = intent.getStringExtra("geoID");
            Log.d("hashmap", "onReceive: " + id);
        }
    };

    private static final int PERMISSIONS_FINE_LOCATIONS = 99;
    private static final int PERMISSIONS_BACKGROUND_LOCATIONS = 101;
    public static final float GEOFENCE_RADIUS = 100;
    public static final String GEOFENCE_ID = java.util.UUID.randomUUID().toString();
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geoFenceHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Bitmap RouteBitMap;
    public static final List<Point> routePoints = new ArrayList<>();
    public static final List<Point> wayPoints = new ArrayList<>();
    public static List<Geofence> geoFences = new ArrayList<>();
    public static HashMap<String, Point> geofenceHashMap = new HashMap<>();
    private TextView routeNameTitle, routeDescTitle, routeTimerLabel, routeTimerValue, startInfo, distanceTextView, routeWayPointInfoName, routeWayPointInfoDesc, altInfo;
    private ImageView routeForward, routeBackward, waypointImage;
    private Button startRouteBtn, waypointInfoBtn, finishRouteBtn;
    private FloatingActionButton fmb;
    private Dialog routeDialog;
    private GoogleMap mMap;
    private Route selectedRoute;
    private Timer routeTimer;
    private TimerTask routeTimerTask;
    private Util utility;
    private Context context;
    private LocationCallback locationCallback;
    private LocationHelper locationHelper = new LocationHelper();
    private LocationRequest locationRequest;
    private Polyline polyline;
    private PolylineOptions options;
    public static LatLng startLatLng;


    /**
     * gathers the route from the previous intent using serizable.
     * add to global list at the top of the class
     * assigns the varibales to their sml ids
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        routeDialog = new Dialog(this);
        context = this;
        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceHelper = new GeoFenceHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Intent intent = getIntent();
        selectedRoute = (Route) intent.getSerializableExtra("Route");
        String selRouteName = selectedRoute.getRouteName();
        routePoints.addAll(selectedRoute.getRoutePoints());
        utility = new Util();

        /**
         * Get the waypoints from the selected route
         */
        for (Point p : routePoints) {
            if (p.getPointType() == PointType.WAYPOINT) {
                wayPoints.add(p);
            }
        }

        ListIterator<Point> pointIterator = wayPoints.listIterator();
        routeNameTitle = findViewById(R.id.tv_RouteName);
        routeDescTitle = findViewById(R.id.tv_RouteDesc);
        routeDescTitle.setBreakStrategy(LineBreaker.BREAK_STRATEGY_HIGH_QUALITY);
        routeBackward = findViewById(R.id.iv_wapointBackBtn);
        routeForward = findViewById(R.id.iv_waypointFrowardBtn);
        startRouteBtn = findViewById(R.id.btn_startRoute);
        finishRouteBtn = findViewById(R.id.btn_finishRouteBtn);
        distanceTextView = findViewById(R.id.tv_disatanceValueID);
        routeWayPointInfoName = findViewById(R.id.routeNameLabelWaypoint);
        routeWayPointInfoDesc = findViewById(R.id.routeDescLabelWayPoint);
        altInfo = findViewById(R.id.tv_AltValueID);
        fmb = findViewById(R.id.fab_MapStyleBtn);
        routeNameTitle.setText(selectedRoute.getRouteName());
        routeDescTitle.setText(selectedRoute.getRouteDesc());
        waypointInfoBtn = findViewById(R.id.btn_waypointInfoBtn);
        routeTimerLabel = findViewById(R.id.tv_timeLabelID);
        routeTimerValue = findViewById(R.id.timevalueID);
        startInfo = findViewById(R.id.tv_RouteStatus);
        RouteBitMap = null;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview_RouteActivity);
        mapFragment.getMapAsync(this::onMapReady);

        /**
         * Moves forward throught the waypoints on screen via the iterator.
         */
        routeForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointIterator.hasNext()) {
                    zoomToPoint(pointIterator.next());
                }

            }

        });

        /**
         * Changes the map type depending on it current state.
         */
        fmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationHelper.changeMapType(v, mMap);
            }
        });

        /**
         * Using the point Iterator, we can cycle back through the waypoints displayed on the map.
         */
        routeBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointIterator.hasPrevious()) {
                    zoomToPoint(pointIterator.previous());
                    Log.d("ROUTE", "onClick: YES");
                }

            }
        });

        /**
         * THis onclick listener will collect information, such as the distance travelled
         * And bundle it into an intent that is then sent to the next activity.
         */
        finishRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                Intent finishIntent = new Intent(RouteActivity.this, RouteOverviewActvitiy.class);
                Bundle finishBundle = new Bundle();
                finishBundle.putString("Timer", utility.getTimerValue());
                String distance = String.valueOf(distanceTextView.getText());
                finishBundle.putString("distance", distance);
                finishIntent.putExtras(finishBundle);
                startActivity(finishIntent);
            }
        });


        /**
         * Location callback is initiated, getting real time updates of the users location
         * This can the be used to track how far has been travelled for example.
         */
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    Log.d("alt", "onLocationResult: no loc");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng locLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("alt", "onLocationResult: " + locLatLng);
                    double alt = location.getAltitude();
                    altInfo.setText(Float.toString(Float.parseFloat(String.format("%.2f", alt))) + "m");
                    Log.d("alt", "onLocationResult: " + alt);
                    distanceTravelled(startLatLng, locLatLng);
                }
            }
        };
    }

    /**
     * WHen called this method will grab the intent information
     * that is selected from the RouteListerActivity.
     */
    public void getIntentRoute() {
        Intent intent = getIntent();
        selectedRoute = (Route) intent.getSerializableExtra("Route");
        String selRouteName = selectedRoute.getRouteName();
    }


    /**
     * Method is called when the map is ready
     * main point for intialising map interface and loading markers.
     *
     * @param googleMap
     */
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        checkDevicePermission();
        mMap.setMyLocationEnabled(true);

        //Check permissions.
        checkLocationPermissions();
        //Call the zoom to route method for the selected route in 'OnCreate'
        zoomToRoute();
        //Populate the map with markers and poyline based on the selected route.
        addMapLinesAndMarkers();
        //Set inital map type to 'Satellite
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //Enable padding
        mMap.setPadding(0, 400, 0, 475);
        //Enable compass on mMap.
        mMap.getUiSettings().setCompassEnabled(true);


        /**
         * override method for onclick listener on the 'Start route' button'
         */
        startRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateMapSnapShot();
                startInfo.setText("Route Started");
                routeTimer = new Timer();
                utility.startTimer(routeTimerValue);
                startLocUpdates();
                startRouteBtn.setEnabled(false);
                startRouteBtn.setBackgroundColor(Color.argb(100, 0, 10, 0));


                if (ActivityCompat.checkSelfPermission(RouteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RouteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermissions();
                    return;
                }
            }
        });


    }

    /**
     * Check if the right permissions are enabled
     * if not the permission is then requested from the user.
     */
    public void checkDevicePermission() {

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_FINE_LOCATIONS);
        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                //access to location
                if (location != null) {
                    startLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
            });
        }
    }

    /**
     * THis Overridden method is called at the start of the activity
     * When called it starts the location updates.
     */
    @Override
    protected void onStart() {
        super.onStart();
        startLocUpdates();
    }

    /**
     * When called this override method will unregister the geofence Broadcast Reciever
     * Aswell as pausing the timr and stopping location updates from the fusedLocationClient.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(geofenceReciver);
        utility.pauseTimer();
        stopLocUpdates();
    }

    /**
     * This method is an override method called when the acivity is resumed,
     * From here the geofence receiver is started as well as the location
     * updates and timer.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(geofenceReciver, new IntentFilter("geoBroad"));
        startLocUpdates();
        if (routeTimer == null) {
            return;
        } else {
            utility.startTimer(routeTimerValue);
        }

    }

    /**
     * This override method is called when the activity lifecycle has ended,
     * from here the location updates are stopped and the timer is ended.
     */
    @Override
    protected void onStop() {
        super.onStop();
        stopLocUpdates();
        utility.pauseTimer();
        routePoints.clear();
        wayPoints.clear();
        geoFences.clear();
    }


    /**
     * Check location permisisons.
     */
    public void checkLocationPermissions() {

        if (ActivityCompat.checkSelfPermission(RouteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show a toast message why we need these permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_FINE_LOCATIONS);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_FINE_LOCATIONS);
            }
        }

    }


    /**
     * This method will take the the point array of two lists to populate the map.
     * using the selected route, the routepoints arraylist populates the polyline to the route on the amp using LatLng postioning.
     * Similarly the waypoint arraylist is used to add markers for each waypoint on the current route.
     */
    public void addMapLinesAndMarkers() {
        MarkerOptions starterMark = new MarkerOptions();
        MarkerOptions endMark = new MarkerOptions();
        MarkerOptions waypointMark = new MarkerOptions();
        starterMark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        waypointMark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        for (Point wp : wayPoints) {
            LatLng wplatng = new LatLng(wp.getLatitude(), wp.getLongitude());

            if (Build.VERSION.SDK_INT >= 29) {
                //we need background permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    addGeoFenceCircle(wplatng, GEOFENCE_RADIUS);
                    addGeoFence(wplatng, GEOFENCE_RADIUS);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        ActivityCompat.requestPermissions
                                (this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSIONS_BACKGROUND_LOCATIONS);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSIONS_BACKGROUND_LOCATIONS);

                    }
                }

            } else {
                addGeoFenceCircle(wplatng, GEOFENCE_RADIUS);
                addGeoFence(wplatng, GEOFENCE_RADIUS);
            }

            List<LatLng> userPointsLatLngs = new ArrayList<>();

            for (Point p : routePoints) {
                LatLng routePointLatLng = new LatLng(p.getLatitude(), p.getLongitude());
                userPointsLatLngs.add(routePointLatLng);

                if(polyline == null) {
                    options = new PolylineOptions()
                            .geodesic(true)
                            .color(Color.WHITE)
                            .width(5);
                    options.addAll(userPointsLatLngs);
                    mMap.addPolyline(options);
                } else {
                    polyline.setPoints(userPointsLatLngs);
                }

                if (p.getPointType() == PointType.START_POINT) {

                    options.startCap(new RoundCap());
                    LatLng markerLatLng = new LatLng(p.getLatitude(), p.getLongitude());
                    starterMark.position(markerLatLng);
                    mMap.addMarker(starterMark);
                    distanceToStart(markerLatLng);

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
    }


    /**
     * This method will take the selected route and zoom too it on the map.
     */
    public void zoomToRoute() {

        LatLng latLng = new LatLng(routePoints.get(1).getLatitude(), routePoints.get(1).getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    /**
     * When called, this method will take the given LatLng from a point
     * and animate the camera to that position.
     *
     * @param p
     */
    public void zoomToPoint(Point p) {
        LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        displayWaypointInfo(p);
    }

    /**
     * This overridden method isz called when the back button on
     * device is pressed to clear the arraylists of the current route
     * that is dynamically populated upon selection.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        routePoints.clear();
        wayPoints.clear();
        geoFences.clear();
    }

    /**
     * This class will create a circle on the map for each
     * Waypoint, that will define how the geofence will look.
     *
     * @param latLng - Position of the waypoint on the map to center the circle.
     * @param radius - Radius, typically defined by a constant to show the area of the geofence.
     */
    public void addGeoFenceCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 0, 200, 200));
        circleOptions.fillColor(Color.argb(65, 0, 200, 200));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }


    /**
     *This method passes in a LatLng and a radius to create a geofence
     * and creates a list of type geofence, using the Geofence Request
     * and pen ding intent, the client can add the geofences based on the given locations.
     * @param latLng  -Location for the Geofence
     * @param radius -  (in metres) for the geofence.
     */
    private void addGeoFence(LatLng latLng, float radius) {

        List<Geofence> geofence = geoFenceHelper.getGeoFence(GEOFENCE_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        GeofencingRequest geofencingRequest = geoFenceHelper.getGeoFencingRequest(geofence);
        PendingIntent pendingIntent = geoFenceHelper.getPendingIntent();
        checkDevicePermission();
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                    }
                });
    }


    /**
     * Overridden method that checks for permissions from the user.
     * @param requestCode - request code for the permission being queried.
     * @param permissions - String array of permissions available.
     * @param grantResults - int array.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_FINE_LOCATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "You cannot use location right now, please check your settings.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSIONS_BACKGROUND_LOCATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // show user that permission is not granted.
            }
        }
    }

    /**
     * This method checks the current location of the user with the passed in destination
     * and determines how far the user is from the location.
     * @param destination
     */
    public void distanceToStart(LatLng destination) {
        checkDevicePermission();
        Task<Location> loc = fusedLocationProviderClient.getLastLocation();
        loc.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                double distance = SphericalUtil.computeDistanceBetween(ll, destination);
                startInfo.setText("DISTANCE TO START IS " + Double.toString(Double.parseDouble(String.format("%.2f", distance / 1000))) + "KM \n PLEASE NAVIGATE TO THE STARTING AREA");
            }
        });


    }

    /**
     *THis method takes two latLng variables, the location at the start of the route and the current location
     * Using the locationCallback the distance travelled is updated incrementaly.
     * @param startLocation
     * @param currLocation
     */
    public void distanceTravelled(LatLng startLocation, LatLng currLocation) {
        float[] results = new float[1];
        Location.distanceBetween(startLocation.latitude, startLocation.longitude, currLocation.latitude, currLocation.longitude, results);
        float distance = results[0];
        distanceTextView.setText(Float.toString(Float.parseFloat(String.format("%.2f", distance/1000))) + "KM");
    }

    /**
     * This Method pass in a point and gathers the current information about it.
     * Informtion is gathered for the Displaying Point Name, description of the point and applicable image.
     * @param p is the object passed into this method.
     */
    public void displayWaypointInfo(Point p) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.layout_btootm_dialog_waypoint, (LinearLayout) findViewById(R.id.bottom_sheet_container));

        routeWayPointInfoName = bottomSheetView.findViewById(R.id.routeNameLabelWaypoint);
        routeWayPointInfoDesc = bottomSheetView.findViewById(R.id.routeDescLabelWayPoint);
        routeWayPointInfoName.setText(selectedRoute.getRouteName());
        routeWayPointInfoDesc.setText(p.getPointDescription());
        waypointImage = (ImageView) bottomSheetView.findViewById(R.id.bottom_sheet_image);
        Glide.with(this).load(Uri.parse(p.getPointImageUri())).into(waypointImage);
        bottomSheetView.findViewById(R.id.bottom_sheet_dismiss_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        DisplayBottomSheet(bottomSheetDialog, bottomSheetView);
    }

    /**
     * This method passes the view and dialog sheet to be presented on th screen when user selects the
     * 'Waypoint info' button
     * @param bSd
     * @param view
     */
    public void DisplayBottomSheet(BottomSheetDialog bSd, View view) {
        waypointInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bSd.setContentView(view);
                bSd.show();
            }
        });
    }


    /**
     * This Method creates a snapshot of the map so that it came be displayed in an image view when the user gathers an overview.
     */
    public void CreateMapSnapShot() {
        zoomToRoute();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //take snapshot
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(@Nullable Bitmap bitmap) {
                       fileFromBitmap(bitmap);

                    }
                });
            }
        });
    }

    /**
     * THis method, when called will start location updates from the locationcallback.
     * Permission check is required before gathering location information.
     */
    private void startLocUpdates() {
        checkDevicePermission();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * Thhis method when called, will stop the 'fusedLocationProvider' updates.
     */
    private void stopLocUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * This Method takes a bitmap and converts it into file by converting it into a byte Array.
     * @param bitmap - the bitmap to be passed in.
     * @return - image file of the bitmap.
     */
    public String fileFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bitToBytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bitToBytes);
            FileOutputStream fileOutput = openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutput.write(bitToBytes.toByteArray());
            bitToBytes.close();
        } catch (Exception e) {
            e.printStackTrace();
            //overwrite each time.
            fileName = null;
        }
        return fileName;
    }
}
