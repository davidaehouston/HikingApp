package houston.david.hikingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import data.Point;
import data.Route;
import data.RouteUserFavouriteAdapter;
import data.routeRvAdapter;

/**
 * THis class is unique to the current signed in user.
 * It will only load if a user is currently signe din, otherwise
 * they are directed to the log in activity.
 */
public class UserDashboardActivity extends AppCompatActivity {

    private TextView userName, userEmail;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;
    private Button logoutButton;
    private RouteUserFavouriteAdapter adapter;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference routeRef = db.collection("AppUsers").document(firebaseAuth.getCurrentUser().getUid()).collection("favouriteRoutes");

    /**
     * Activity start method.
     * Checks if a current user is already signed in,
     * if not they are directed to the log in activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        userName = findViewById(R.id.accountName);
        userEmail = findViewById(R.id.accountEmail);
        logoutButton = findViewById(R.id.btn_Logout);

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(UserDashboardActivity.this, UserLoginActivity.class));
            Toast.makeText(this, "No User Currently Logged In, Please Log in to See Account Details", Toast.LENGTH_LONG).show();
        } else {
            userID = firebaseAuth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("AppUsers")
                    .document(userID);

            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    userEmail.setText("Email : " +value.getString("userEmail"));
                    userName.setText("Name : "+value.getString("firstName") + " " + value.getString("lastName"));
                }
            });
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        userFavourites();
    }

    /**
     * This method when called will sign out the current user, using firebase Auth.
     */
    private void logoutUser() {
        firebaseAuth.signOut();
        Toast.makeText(this, "User Signed Out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(UserDashboardActivity.this, MainActivity.class));
    }

    private void userFavourites() {
        Query query = routeRef.orderBy("routeName",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Route> opts = new FirestoreRecyclerOptions.Builder<Route>()
                .setQuery(query, Route.class)
                .build();

        adapter = new RouteUserFavouriteAdapter(opts);
        RecyclerView recyclerView = findViewById(R.id.rv_userRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new routeRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(DocumentSnapshot snapshot, int position) {

                Route route = snapshot.toObject(Route.class);
                List<Point> listP = route.getRoutePoints();
                Intent intent = new Intent(UserDashboardActivity.this, RouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Route", route);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onFavouriteClicked(DocumentSnapshot snapshot, int position) {

            }

        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteRoute(viewHolder.getAbsoluteAdapterPosition());
                Toast.makeText(UserDashboardActivity.this, "Route Has Been Deleted From Favourites.", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }
}