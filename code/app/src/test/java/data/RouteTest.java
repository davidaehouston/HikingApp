package data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class RouteTest extends Point {

    Route r;
    int uidValid;
    String routeNameValid, routeDescValid;
    List<Point> testRoutePoints;

    Double latitudeValid,longitudeValid;
    String pointDescValid, pointUriValid;
    PointType pointTypeValid;

    @Before
    public void setUp() throws Exception {

        latitudeValid = 5.055678;
        longitudeValid = -60.68697;
        pointDescValid = "testPointDesc";
        pointUriValid = "testPointUri";
        pointTypeValid = PointType.STANDARD_POINT;

        r = new Route();

        uidValid = 24567;
        routeNameValid = "ValidRouteName";
        routeDescValid = "ValidRouteDesc";
        testRoutePoints = new ArrayList<>();
        testRoutePoints.add(new Point(latitudeValid, longitudeValid, pointDescValid, pointTypeValid, pointUriValid));


    }

    @Test
    public void testDefaultConstructor() {
        r = new Route();
        assertNotNull(r);
    }

    @Test
    public void testConstructorWithArgs() {
        r = new Route(routeNameValid, routeDescValid, testRoutePoints);
        assertEquals(routeNameValid, r.getRouteName());
        assertEquals(routeDescValid, r.getRouteDesc());
        assertEquals(testRoutePoints, r.getRoutePoints());
    }

    @Test
    public void getSetUid() {

        r = new Route();
        r.setUid(uidValid);
        Assertions.assertEquals(uidValid, r.getUid());
    }

    @Test
    public void getSetRouteName() {
        r = new Route();
        r.setRouteName(routeNameValid);
        assertEquals(routeNameValid, r.getRouteName());
    }

    @Test
    public void getSetRouteDesc() {
        r = new Route();
        r.setRouteDesc(routeDescValid);
        assertEquals(routeDescValid, r.getRouteDesc());
    }

    @Test
    public void getSetRoutePoints() {
        r = new Route();
        r.setRoutePoints(testRoutePoints);
        assertEquals(testRoutePoints, r.getRoutePoints());
    }
}