package data;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Converter class converts points to json and back
 *
 */
public class Converter {

    /**
     * Converts from points to list Object.
     * @param points
     * @return
     */
    @TypeConverter
    public String fromPointsList(List<Point> points) {
        if(points == null) {
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Point>>() {
        }.getType();
        String json = gson.toJson(points, type);
        return json;
    }

    /**
     * Converts from list to point Object.
     * @param pointsString
     * @return
     */
    @TypeConverter
    public List<Point> toPpintsList (String pointsString) {
        if(pointsString == null) {
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Point>>() {
        }.getType();
        List<Point> listpoints = gson.fromJson(pointsString, type);
        return listpoints;

    }

}
