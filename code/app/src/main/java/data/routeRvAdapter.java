package data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import houston.david.hikingapp.R;

/**
 * Adapter for RecyclerView, class
 * using Firestore.
 */
public class routeRvAdapter extends FirestoreRecyclerAdapter<Route, routeRvAdapter.RouteHolder> {

    //create onclick listener variable
    private OnItemClickListener listener;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    ImageView btn_Fav;

    /**
     * This method checks if two items in the recycler view are exactly
     * the same.
     */
    private static final DiffUtil.ItemCallback<Route> DIFF_CALLBACK = new DiffUtil.ItemCallback<Route>() {
        @Override
        public boolean areItemsTheSame(@NonNull Route oldItem, @NonNull Route newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        /**
         * This method will check if the old item contents are the same
         * as the ew items.
         * @param oldItem
         * @param newItem
         * @return
         */
        @Override
        public boolean areContentsTheSame(@NonNull Route oldItem, @NonNull Route newItem) {
            return oldItem.getRouteName().equals(newItem.getRouteName()) &&
                    oldItem.getRouteDesc().equals(newItem.getRouteDesc());
        }
    };


    public routeRvAdapter(@NonNull FirestoreRecyclerOptions<Route> options) {
        super(options);
    }

    /**
     * Create and return a viewholder
     * @param parent - parent view group context for recycler view
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RouteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create a view and inflate, passing in view item for the recucler view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_rv_item, parent,false);

        //return the new itemview
        return new RouteHolder(itemView);
    }


    /**
     * Assigns the java objects in the items in recycler view.
     * @param holder - viewholder being used.
     * @param position - postion in the recycler list.
     * @param model - object being passed in.
     */
    @Override
    protected void onBindViewHolder(@NonNull RouteHolder holder, int position, @NonNull Route model) {
        holder.routeNameTextView.setText(model.getRouteName());
        holder.routeDescTextView.setText(model.getRouteDesc());

    }

    /**
     * Inner viewholder class, holds
     * the views within the viewholder items
     *
     */
    class RouteHolder extends RecyclerView.ViewHolder {

        //create variables for what is viewed in itemlist
        private TextView routeNameTextView;
        private TextView routeDescTextView;

        //note holder constructor
        public RouteHolder(@NonNull View itemView) {
            super(itemView);
            //assign ids to items.
            routeNameTextView = itemView.findViewById(R.id.route_rv_name);
            routeDescTextView = itemView.findViewById(R.id.route_rv_desc);
            btn_Fav = (ImageView) itemView.findViewById(R.id.btn_favourite);
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();

          btn_Fav.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  int position = getAbsoluteAdapterPosition();
                  if(listener!= null && position != RecyclerView.NO_POSITION) {
                      listener.onFavouriteClicked(getSnapshots().getSnapshot(position),position);
                      if (user != null) {
                          v.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                      }
                  }
              }
          });

            //onclick listener for each item in the list.
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

    /**
     * Inner Interface for onitem clicked
     */
    public interface OnItemClickListener {
        void onItemClicked(DocumentSnapshot snapshot, int position);
        void onFavouriteClicked(DocumentSnapshot snapshot, int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void deleteRoute(int position) {
        getSnapshots().getSnapshot(position).getReference()
                .delete();
    }

}
