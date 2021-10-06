package data;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class RouteData extends Application {

    private static RouteData singleton;

    private List<Route> myRoutes = new ArrayList<>();

    public RouteData getInstance() {

        return singleton;
    }

    public void onCreate() {
        super.onCreate();
        singleton= this;
        myRoutes = new ArrayList<>();
    };

    public List<Route> getMyRoutes() {
        return myRoutes;
    }

    public void setMyRoutes(List<Route> myRoutes) {
        this.myRoutes = myRoutes;
    }



}
