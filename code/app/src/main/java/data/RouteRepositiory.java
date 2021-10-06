package data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * MVVM repository class, handles internal storage
 * aspects of the application
 * Create's the methods for our API that exposes the database operations.
 */
public class RouteRepositiory {

    //Create variables
    private RouteDao routeDao;
    private LiveData<List<Route>> allRoutes;

    /**
     * Constructor for the class
     * Passes application for viewModel.
     * @param application
     */
    public RouteRepositiory(Application application) {
        //get instance of database
        RouteDatabase database = RouteDatabase.getInstance(application);
        //assign variables
        routeDao = database.routeDao();
        allRoutes = routeDao.getAllRoutes();
    }


    /**
     * Insert database operation
     * @param route
     */
    public void insert(Route route) {
        //create instance of inset inner class and excute and pass in Route
        new InsertRouteAsyncTask(routeDao).execute(route);
    }

    /**
     * Update database operation
     * @param route
     */
    public void update(Route route) {
        //create instance of update inner class and excute and pass in Route
        new updateRouteAsyncTask(routeDao).execute(route);
    }

    /**
     * delete database operation
     * @param route
     */
    public void delete(Route route) {
        //create instance of delete inner class and excute and pass in Route
        new deleteRouteAsyncTask(routeDao).execute(route);

    }

    /**
     * delete all notes database operation
     */
    public void deleteAllRoutes() {
        //create instance of delete inner class and excute and pass in Route
        new deleteAllRouteAsyncTask(routeDao).execute();

    }

    /**
     * Returns live data list object of all Routes
     * from RouteDao
     * @return
     */
    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }


    /**
     * Static inner class, using an aysnc task to run on background thread.
     * Takes route as a parameter.
     */
    private static class InsertRouteAsyncTask extends AsyncTask<Route, Void, Void> {

        //Create var for routeDao for database operations
        private RouteDao routeDao;

        //Use interface constructor.
        private InsertRouteAsyncTask(RouteDao routeDao) {
             this.routeDao = routeDao;
        }

        //passes Route at index 0 (first index)
        @Override
        protected Void doInBackground(Route... routes) {
            routeDao.insertRoute(routes[0]);
            return null;
        }
    }

    /**
     * Static inner class, using an aysnc task to run on background thread.
     * Takes route as a parameter.
     */
    private static class updateRouteAsyncTask extends AsyncTask<Route, Void, Void> {

        //Create var for routeDao for database operations
        private RouteDao routeDao;

        //Use interface constructor.
        private updateRouteAsyncTask(RouteDao routeDao) {
            this.routeDao = routeDao;
        }

        //passes Route at index 0 (first index)
        @Override
        protected Void doInBackground(Route... routes) {
            routeDao.updateRoute(routes[0]);
            return null;
        }
    }

    /**
     * Static inner class, using an aysnc task to run on background thread.
     * Takes route as a parameter.
     */
    private static class deleteRouteAsyncTask extends AsyncTask<Route, Void, Void> {

        //Create var for routeDao for database operations
        private RouteDao routeDao;

        //Use interface constructor.
        private deleteRouteAsyncTask(RouteDao routeDao) {
            this.routeDao = routeDao;
        }

        //passes Route at index 0 (first index)
        @Override
        protected Void doInBackground(Route... routes) {
            routeDao.deleteRoute(routes[0]);
            return null;
        }
    }

    /**
     * Static inner class, using an aysnc task to run on background thread.
     * Takes route as a parameter.
     */
    private static class deleteAllRouteAsyncTask extends AsyncTask<Void, Void, Void> {

        //Create var for routeDao for database operations
        private RouteDao routeDao;

        //Use interface constructor.
        private deleteAllRouteAsyncTask(RouteDao routeDao) {
            this.routeDao = routeDao;
        }

        //passes Route at index 0 (first index)
        @Override
        protected Void doInBackground(Void... Void) {
            routeDao.deleteAllRoutes();
            return null;
        }
    }

}
