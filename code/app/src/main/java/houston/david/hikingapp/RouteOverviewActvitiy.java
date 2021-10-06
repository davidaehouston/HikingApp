package houston.david.hikingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;

/**
 * This class displays an overview of the route taken by the user.
 * Displays how long it took and how far was travelled.
 */
public class RouteOverviewActvitiy extends AppCompatActivity {

    TextView routeTime, distanceTravelled;
    ImageView mapOverView;
    FloatingActionButton fab;

    /**
     * This method starts the activity, it grabs the information from the Route
     * acivity via intent to display to the user.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_overview);

        routeTime = findViewById(R.id.tv_TotalTime);
        mapOverView = findViewById(R.id.img_Map);
        distanceTravelled = findViewById(R.id.tv_totalDistanceTravelled);

        Intent overviewIntent = getIntent();
        Bundle b = overviewIntent.getExtras();

        String timer = b.getString("Timer");
        routeTime.setText("Completed in : " + timer);
        String distance = b.getString("distance");
        distanceTravelled.setText("Distance Travelled : "+distance);

        fab = findViewById(R.id.fac_Close);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RouteOverviewActvitiy.this,MainActivity.class));
            }
        });

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(RouteOverviewActvitiy.this
                    .openFileInput("myImage"));
            Glide.with(this).load(bitmap).into(mapOverView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}