package data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class PointTest {

    Double latitudeValid,longitudeValid;
    String pointDescValid, pointUriValid;
    PointType pointTypeValid;
    Point p;

    @Before
    public void setUp() throws Exception {

        p = new Point();

        latitudeValid = 5.055678;
        longitudeValid = -60.68697;
        pointDescValid = "testPointDesc";
        pointUriValid = "testPointUri";
        pointTypeValid = PointType.STANDARD_POINT;
    }

    @Test
    public void testDefaultConstructor() {
        p = new Point();
        Assertions.assertNotNull(p);
    }

    @Test
    public void testConstructorWithArgs() {
        p = new Point(latitudeValid, longitudeValid, pointDescValid, pointTypeValid, pointUriValid);

        Assertions.assertEquals(latitudeValid, p.getLatitude());
        Assertions.assertEquals(longitudeValid, p.getLongitude());
        assertEquals(pointDescValid, p.getPointDescription());
        assertEquals(pointTypeValid, p.getPointType());
        assertEquals(pointUriValid, p.getPointImageUri());


    }

    @Test
    public void testgetSetLatitude() {
        p = new Point();
        p.setLatitude(latitudeValid);
        Assertions.assertEquals(latitudeValid, p.getLatitude());
    }

    @Test
    public void testgetSetLongitude() {
        p = new Point();
        p.setLongitude(longitudeValid);
        Assertions.assertEquals(longitudeValid, p.getLongitude());
    }

    @Test
    public void testgetSetPointDescription() {
        p = new Point();
        p.setPointDescription(pointDescValid);
        Assertions.assertEquals(pointDescValid, p.getPointDescription());
    }

    @Test
    public void testgetSetPointType() {
        p = new Point();
        p.setPointType(pointTypeValid);
        Assertions.assertEquals(pointTypeValid, p.getPointType());
    }

    @Test
    public void testgetSetPointUri() {
        p = new Point();
        p.setPointType(PointType.WAYPOINT);
        p.setPointImageUri(pointUriValid);
        Assertions.assertEquals(pointUriValid, p.getPointImageUri());
    }

    @Test
    public void testgetSetDefaultPointUri() {
        p = new Point();
        p.setPointImageUri(Point.URI_DEFAULT);
        Assertions.assertEquals(Point.URI_DEFAULT, p.getPointImageUri());
    }




}