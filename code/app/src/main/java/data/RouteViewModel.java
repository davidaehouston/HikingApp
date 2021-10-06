package data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * MVVM ViewModel class for Routes
 * Request data from repository to display in activity.
 */
public class RouteViewModel extends AndroidViewModel {

    //create variables
    private RouteRepositiory repositiory;
    private LiveData<List<Route>> allRoutes;


    /**
     * Constructor for the viewmodel
     * Can pass in application context when needed.
     * Which is independant of all activites.
     * @param application
     */
    public RouteViewModel(@NonNull Application application) {
        super(application);

        //instantiate variables
        repositiory = new RouteRepositiory(application);
        allRoutes = repositiory.getAllRoutes();
    }


    /**
     *  wrapper method for database operation
     */
    public void insert(Route route) {
        repositiory.insert(route);
    }

    /**
     *  wrapper method for database operation
     */
    public void update(Route route) {
        repositiory.update(route);
    }

    /**
     *  wrapper method for database operation
     */
    public void delete(Route route) {
        repositiory.delete(route);
    }

    /**
     *  wrapper method for database operation
     */
    public void deleteAllRoutes() {
        repositiory.deleteAllRoutes();
    }

    /**
     *  wrapper method for database operation
     */
    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }
}
