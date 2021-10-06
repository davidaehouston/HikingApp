package houston.david.hikingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import util.Constants;
import util.LocationHelper;

/***
 * Author David Houston
 * This class is the landing page for the application
 * When granted, It displays the users current location on a map
 * Presnts the user with two options, to follow a route or create their own.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {

    //Constants

    //Google's API for location services
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MapView mapView;
    private Button pickRouteBtn, createRouteBtn;
    private GoogleMap mMap;
    private FloatingActionButton mapTypeButton;
    private LocationHelper locationHelper;
    private FirebaseAuth auth;
    FirebaseUser user;



    /**
     * OnCreate this is the main stater of the application.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mapView = findViewById(R.id.mapview_RouteActivtiyMapView);
        mapTypeButton = findViewById(R.id.fab_MapStyleBtn);
        pickRouteBtn = findViewById(R.id.btn_finishRouteBtn);
        createRouteBtn = findViewById(R.id.btn_createRouteBtn);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationHelper = new LocationHelper();

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);

        pickRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RouteListerActivity.class);
                startActivity(intent);
            }
        });

        createRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,createRouteActivity.class);
                startActivity(intent);
            }
        });

        mapTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationHelper.changeMapType(v,mMap);
            }
        });

    }


    /**
     * On Map ready initalizes the map, checks user permissions
     * Adds polyine objects from test route to map.
     * @param googleMap
     */
    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setPadding(25, 0, 0, 375);
        enableUserLocation();
        zoomToUserLocation();
    }

    /**
     * THis method will zoom in on the current location on the map when active.
     */
    private void zoomToUserLocation() {
        @SuppressLint("MissingPermission") Task<Location> locTask = fusedLocationProviderClient.getLastLocation();
        locTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            }
        });
    }


    /**
     * This method will enable users location when activated.
     */
    public void enableUserLocation() {
       if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
       PackageManager.PERMISSION_GRANTED) {
           mMap.setMyLocationEnabled(true);
       } else {
           //ask for permission
           if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
               //we need to show user a dialog to explain why permissions are needed, and then aks for the permmsision.
               ActivityCompat.requestPermissions
                       (this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSIONS_FINE_LOCATIONS);
           } else {
               ActivityCompat.requestPermissions
                       (this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSIONS_FINE_LOCATIONS);
           }
       }
    }



    /**
     * Overridden method: this method will look to see of permisison are granted.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.PERMISSIONS_FINE_LOCATIONS) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            } else {
                // show user that permission is not granted.
            }
        }
        if (requestCode == Constants.PERMISSIONS_FINE_LOCATIONS) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // show user that permission is not granted.
            }
        }
    }

    /**
     * inflates the popupMenu on screen
     * @param view
     */
    public void showMenu(View view) {
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        PopupMenu popupMenu = new PopupMenu(this, view);

        if (user == null) {
            popupMenu.inflate(R.menu.popupmenu);
            popupMenu.show();
            popupMenu.getMenu().findItem(R.id.accountItem).setEnabled(false);
        } else {
            popupMenu.inflate(R.menu.popupmenu);
            popupMenu.show();
            popupMenu.getMenu().findItem(R.id.loginItem).setEnabled(false);
        }



        popupMenu.setOnMenuItemClickListener(this);


    }

    /**
     * This method will be invoked when a menu item is clicked if the item
     * itself did not already handle the event.
     *
     * @param item the menu item that was clicked
     * @return {@code true} if the event was handled, {@code false}
     * otherwise
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.loginItem:

               startActivity(new Intent(this, UserLoginActivity.class));
               return true;
            case R.id.accountItem:
                startActivity(new Intent(this, UserDashboardActivity.class));
                return true;
            default:
                return false;
        }

    }

}

