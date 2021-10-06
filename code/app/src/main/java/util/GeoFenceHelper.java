

package util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * This class
 */
public class GeoFenceHelper extends ContextWrapper {

    //create a random ID for each Geofence.
    private static final java.util.UUID UUID = null;
    PendingIntent pendingIntent;

    /** Constructor
     * @param base
     */
    public GeoFenceHelper(Context base) {
        super(base);
    }

    /**
     * Creates and returns a geofencing request,
     * sets the intial trigger types, and adds the required
     * Geofences (list) as a parameter.
     * @param geofence
     * @return
     */
    public GeofencingRequest getGeoFencingRequest(List<Geofence> geofence) {
        return new GeofencingRequest.Builder()
                .addGeofences(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    /**
     * returns a list of geofences, passing in their ID, position, radius
     * and transition type.
     * @param ID - ID of the geofence
     * @param latLng - latitude and Longitude for geofence
     * @param radius - Radius of the geofence.
     * @param transitionTypes - Transition type of each geofence.
     * @return - list of geofences.
     */
    public List <Geofence> getGeoFence (String ID, LatLng latLng, float radius, int transitionTypes) {
        List<Geofence> returnGeoFenceList = new ArrayList<>();
        String id = UUID.randomUUID().toString();

        returnGeoFenceList.add(
                new Geofence.Builder()
                        .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                        .setRequestId(id)
                        .setTransitionTypes(transitionTypes)
                        .setLoiteringDelay(2000)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .build());

        return returnGeoFenceList;
    }

    /**
     *Creates a pending intent to send broadcast to geofenceBoradcastReciver.
     * @return
     */
    public PendingIntent getPendingIntent() {

        if(pendingIntent != null) {
            return  pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

}

