package util;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

public class LocationHelper {


    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;



    public void setLocationRequest(LocationRequest locationRequest) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    /**
     * This method when called will change the map type
     * depending on its current type.
     * @param view
     * @param googleMap
     */
    public void changeMapType(View view, GoogleMap googleMap) {
        if (googleMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            Toast.makeText(view.getContext(), "Map Type changed to Satellite", Toast.LENGTH_SHORT).show();
        } else if (googleMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            Toast.makeText(view.getContext(), "Map Type changed to Terrain", Toast.LENGTH_SHORT).show();
        } else if (googleMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Toast.makeText(view.getContext(), "Map Type changed to Normal", Toast.LENGTH_SHORT).show();
        }
    }




}
