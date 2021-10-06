package houston.david.hikingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import data.Point;
import data.Route;
import data.RouteViewModel;

/**
 * This class is will save the route recorded by the user to firestore database.
 */
public class RouteCreationOverviewActivity extends AppCompatActivity {

    TextView routeCreateTitle, routeCreateDesc;
    EditText routeCreateaddTitle, routeCreateAddDesc;
    Button saveRouteButton;
    RouteViewModel routeViewModel;
    FloatingActionButton fmb;

    /**
     * Starter method for this activity, assigns id to variables and on click listeners.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route_info);

        routeCreateaddTitle = findViewById(R.id.routeName_save);
        routeCreateAddDesc = findViewById(R.id.routeDesc_save);
        saveRouteButton = findViewById(R.id.btn_saveRouteBtn);
        fmb = findViewById(R.id.fmb_cancelSave);

        saveRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRoute();
            }
        });

        fmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * THis method when called, will take the data given by the user
     * data is checked for validation
     * and added to the firestore database.
     */
    public void saveRoute() {

        Intent intent = getIntent();

        String routeName = routeCreateaddTitle.getText().toString().trim();
        String routeDesc = routeCreateAddDesc.getText().toString().trim();
        List<Point> routePoints = (List<Point>) intent.getSerializableExtra("routePoints");

        if (routeName.isEmpty()) {
            routeCreateaddTitle.setError("Please provide a route Name");
            routeCreateaddTitle.requestFocus();

        } else if (routeDesc.isEmpty() | routeDesc.length() > 160) {
            routeCreateAddDesc.setError("Please enter a description between 1 & 160 characters.");
            routeCreateAddDesc.requestFocus();

        } else {

            CollectionReference routeRef = FirebaseFirestore.getInstance()
                    .collection("Routes");
            routeRef.add(new Route(routeName, routeDesc, routePoints));
            Toast.makeText(RouteCreationOverviewActivity.this, "Route Added", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RouteCreationOverviewActivity.this, MainActivity.class));
        }
    }

}