package houston.david.hikingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import data.*;

/**
 * This class uses a list view with a custom adapter from the routeData singleton.
 */
public class RouteListerActivity extends AppCompatActivity {

    public RouteViewModel routeViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference routeRef = db.collection("Routes");
    private routeRvAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ImageButton favButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_lister);
        favButton = findViewById(R.id.btn_favRoute_null);
        InitRecyclerView();
    }

    /**
     * this method is responsible for initiating the recyclerview
     * via a firestore query.
     * allows a user to select from one of the items in the list or delete them with a swipe gesture.
     */
    private void InitRecyclerView() {
        Query query = routeRef.orderBy("routeName", Query.Direction.DESCENDING );
        FirestoreRecyclerOptions<Route> options = new FirestoreRecyclerOptions.Builder<Route>()
                .setQuery(query, Route.class)
                .build();

        adapter = new routeRvAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new routeRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(DocumentSnapshot snapshot, int position) {

                Route route = snapshot.toObject(Route.class);
                List<Point> listP = route.getRoutePoints();
                Intent intent = new Intent(RouteListerActivity.this, RouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Route", route);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onFavouriteClicked(DocumentSnapshot snapshot, int position) {
                Route route = snapshot.toObject(Route.class);
                user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null ) {
                    String uid = user.getUid();
                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    CollectionReference uidRef = rootRef.collection("AppUsers").document(uid).collection("favouriteRoutes");
                    uidRef.add(route);
                    Toast.makeText(RouteListerActivity.this, "Route added to Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RouteListerActivity.this, "Unable to add to favourites, please make sure you are logged in.", Toast.LENGTH_SHORT).show();
                }

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
                Toast.makeText(RouteListerActivity.this, "Route Has Been Deleted.", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

    }


    /**
     * When this method is started the adapter
     * will listen for changes in the viewmodel.
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    /**
     * When this method is called, the adapter
     * will stop listening for changes in the view model.
     */
    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }


}