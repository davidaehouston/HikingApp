package data;

import android.os.Parcel;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Route class, object class for Routes,
 * Also works as the entity for ROOM database.
 */
@Entity(tableName = "routes_table")
public class Route extends Point implements Serializable {



   //Instance Vars
    /**
     * Primary key incremented for each route
     * when added to ROOM databse.
     */
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "route_Name")
    private String routeName;

    @ColumnInfo(name = "route_Desc")
    private String routeDesc;

    /**
     * Converter to string for better parsing in room.
     */
    @ColumnInfo(name = "route_Ponts")
    @TypeConverters(Converter.class)
    private List<Point> routePoints = new ArrayList<>();

    /**
     * Default constructor
     * takes no arguments.
     */
    public Route() {
    }

    /**
     * Constructor with arguements.
     * @param routeName - Name of the Route
     * @param routeDesc - Description for the Route
     * @param routePoints - List of Type Point for the route.
     */
    public Route(String routeName, String routeDesc, List<Point> routePoints) {
        this.routeName = routeName;
        this.routeDesc = routeDesc;
        this.routePoints = routePoints;
    }


    /**
     * Get the unique identifier for this route
     * Useful for ROOM database.
     * @return
     */
    public int getUid() {
        return uid;
    }

    /**
     * Sets the Unique Identifier
     * @param uid
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * Gets the route name
     * @return
     */
    public String getRouteName() {
        return routeName;
    }

    /**
     * Sets the routes name
     * @param routeName
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    /**
     * Gets the Routes Description
     * @return
     */
    public String getRouteDesc() {
        return routeDesc;
    }

    /**
     * Sets the Routes Description
     * @param routeDesc
     */
    public void setRouteDesc(String routeDesc) {
        this.routeDesc = routeDesc;
    }

    /**
     * Get the List of Type 'Point' for the Route.
     * @return
     */
    public List<Point> getRoutePoints() {
        return routePoints;
    }

    /**
     * Sets the List of type 'Point' for the route.
     * @param routePoints
     */
    public void setRoutePoints(List<Point> routePoints) {
        this.routePoints = routePoints;
    }


    /**
     * Can be called when logging or debugging.
     * @return
     */
    @Override
    public String toString() {
        return "route{" +
                "uid=" + uid +
                ", routeName='" + routeName + '\'' +
                ", routeDesc='" + routeDesc + '\'' +
                ", routePoints=" + routePoints +
                '}';
    }
}
