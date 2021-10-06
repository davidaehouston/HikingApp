package data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object Interface Class
 * For ROOM databse.
 */
@Dao
public interface RouteDao {

    /**
     * Insert Query a Route object is passed in
     * to this query.
     * @param route
     */
    @Insert
    void insertRoute(Route route);

    /**
     * Update Query a Route object is passed in
     * to this query
     * @param route
     */
    @Update
    void updateRoute(Route route);

    /**
     * Delete Query a Route object is passed in
     * to this query
     * @param route
     */
    @Delete
    void deleteRoute(Route route);

    /**
     * Delete all Query, this can delete all routes.
     * Doesn't pass any parameters.
     */
    @Query("DELETE FROM routes_table")
    void deleteAllRoutes();

    /**
     * Collects and returns all the routes in the 'routes_table'
     * Returned as a live Data List object.
     * @return
     */
    @Query("SELECT * FROM routes_table ORDER BY uid ASC")
    LiveData<List<Route>> getAllRoutes();
}
