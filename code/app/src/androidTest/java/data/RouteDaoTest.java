package data;

import static org.junit.Assert.*;

import android.content.Context;


import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RouteDaoTest {

    private RouteDatabase routeDatabase;
    private RouteDao routeDao;
    private String testRouteName,testRouteDesc;
    private List<Point> testRoutePoints;

    @Before
    public void createDb() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        routeDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), RouteDatabase.class)
                .build();
        routeDao = routeDatabase.routeDao();

        testRouteDesc = "this is a test route";
        testRouteName = "testRoute";
        testRoutePoints = new ArrayList<>();

    }

    @Test
    public void insertRouteDatabaseTest() {
        Route r = new Route(testRouteName, testRouteDesc, testRoutePoints);
        routeDao.insertRoute(r);
        LiveData<List<Route>> getTestRoute = routeDao.getAllRoutes();
        assertEquals(getTestRoute.getValue().get(0), r);

    }


    @After
    public void closeDb() throws Exception {

    }
}