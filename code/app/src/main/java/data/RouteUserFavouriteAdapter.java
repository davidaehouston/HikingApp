package data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import houston.david.hikingapp.R;

/**
 * Adapter for RecyclerView, class
 * using Firestore.
 */
public class RouteUserFavouriteAdapter extends FirestoreRecyclerAdapter<Route, RouteUserFavouriteAdapter.RouteHolder> {

    private routeRvAdapter.OnItemClickListener listener;

    class RouteHolder extends RecyclerView.ViewHolder {

        TextView routeName;
        TextView routeDesc;
        Button Btn_fav;

        public RouteHolder(@NonNull View itemView) {
            super(itemView);
            routeName = itemView.findViewById(R.id.route_rv_name);
            routeDesc = itemView.findViewById(R.id.route_rv_desc);
            Btn_fav = itemView.findViewById(R.id.btn_favRoute_null);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if(listener!= null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClicked(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }


    public RouteUserFavouriteAdapter(@NonNull FirestoreRecyclerOptions<Route> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RouteUserFavouriteAdapter.RouteHolder holder, int position, @NonNull Route model) {
        holder.routeName.setText(model.getRouteName());
        holder.routeDesc.setText(model.getRouteDesc());
    }


    @NonNull
    @Override
    public RouteUserFavouriteAdapter.RouteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_rvuser_item, parent,false);
        return new RouteHolder(itemView);
    }


    public void setOnItemClickListener(routeRvAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public void deleteRoute(int position) {
        getSnapshots().getSnapshot(position).getReference()
                .delete();
    }

}
