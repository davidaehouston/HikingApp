package util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.io.Serializable;
import java.util.List;

import houston.david.hikingapp.RouteActivity;

/**
 * This class is the broadcast event
 * receiver when a geofence is triggered
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    //create variable
    NotifactionHelper notifactionHelper;

    /**
     * When called capture the geofence event
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        notifactionHelper = new NotifactionHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        //check of there is an error with the geofence.
        if (geofencingEvent.hasError()) {
            Log.d("geoferror", "onReceive: Geofence even has error..");
        }

        //get list of triggered geofences
        List<Geofence> triggeredGeofenceList = geofencingEvent.getTriggeringGeofences();

        //get the geofence request ID
        for (Geofence geofence : triggeredGeofenceList) {
            Log.d("GEOF", "onReceive: "+geofence.getRequestId());

        }

        //get location of each triggered geofence.
        Location triggerLocation = geofencingEvent.getTriggeringLocation();

        double lat = triggerLocation.getLatitude();
        double lon = triggerLocation.getLongitude();

        //check the type of trigger that was initalised.
        int transitionType = geofencingEvent.getGeofenceTransition();
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "Entered Geofence", Toast.LENGTH_SHORT).show();
                Log.d("GEOF", "onReceive: "+geofencingEvent.getGeofenceTransition());
                notifactionHelper.sendNotification("A Waypoint is Nearby!", "Open the Application to find out more", RouteActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "Dwelling inside of Geofence", Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "Exited Geofence area", Toast.LENGTH_SHORT).show();
                break;



        }

        //bundle triggered information to send to other acitvies
        //this need further implementation.
        Bundle b = intent.getExtras();
        Intent i = new Intent("TESTER");
        i.putExtra("lat", lat);
        i.putExtra("lon", lon);
        i.putExtras(b);
        context.sendBroadcast(i);


    }

}