package data;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.Serializable;

import houston.david.hikingapp.R;

/**
 * Point class, this class is a Object class with getters and setters
 */
public class Point implements Serializable {

    //Constant for uri when image not supplied.
    public static final String URI_DEFAULT = "android.resource://houston.david.hikingapp/drawable/"+R.drawable.uridefult;

    /*Instance Vars*/
    private double latitude;
    private double longitude;
    private String pointDescription;
    private PointType pointType;
    private String pointImageUri;


    /**
     * Default constructor, takes no arguments.
     */
    public Point() {
    }

    /**
     * This is a constructor with agruments.
     * @param latitude
     * @param longitude
     * @param pointDescription
     * @param pointType
     * @param pointImageUri
     */
    public Point(double latitude, double longitude, String pointDescription, PointType pointType, String pointImageUri) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointDescription = pointDescription;
        this.pointType = pointType;
        this.pointImageUri = pointImageUri;
    }


    /**
     * getter for the latitude.
     * @return -returns the latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Setter for the lattitde
     * @param latitude - the latitude that is set.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter for the Longitude.
     * @return - returns the longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Setter for the longitude
     * @param longitude - the Longitude that is set.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter for the Point `Description.
     * @return - the Getter for the Point Description.
     */
    public String getPointDescription() {
        return pointDescription;
    }

    /**
     * The Setter for the Point description
     * @param pointDescription - the Description to be Set.
     */
    public void setPointDescription(String pointDescription) {
        this.pointDescription = pointDescription;
    }

    /**
     * Getter the Enum for point type.
     * @return - The Enum PointType.
     */
    public PointType getPointType() {
        return pointType;
    }

    /**
     * Setter for the point type.
     * @param pointType - sethe the point type.
     */
    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    /**
     * Getter for image uri, this gets the string path to the image of the point.
     * @return - the stirng image uri.
     */
    public String getPointImageUri() {
        return pointImageUri;
    }

    /**
     * Setter for the string uri, if the waypoint type is not 'WAYPOINT'
     * Then the default uri value is set.
     * @param pointImageUri
     */
    public void setPointImageUri(String pointImageUri) {
        this.pointImageUri = pointImageUri;
    }


    /**
     * To string method, mostly for logging purposes.
     * @return
     */
    @Override
    public String toString() {
        return "Point{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", pointDescription='" + pointDescription + '\'' +
                ", pointType=" + pointType +
                ", pointImageUri=" + pointImageUri +
                '}';
    }
}