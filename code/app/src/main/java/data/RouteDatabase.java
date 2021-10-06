package data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Project database class, using a version schema that requires updating if
 * any changes to the database structure are made.
 */
@Database(entities = {Route.class}, version = 2)
public abstract class RouteDatabase extends RoomDatabase {
    //Create an instance of the database
    private static RouteDatabase instance;

    //abstract Dao
    public abstract RouteDao routeDao();

    /**
     * create the instance of the Database,
     * check if it is null and it not the return current instance.
     * @param context - context of the current application
     * @return - instance of the database.
     */
    public static synchronized RouteDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), RouteDatabase.class, "route_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    /**
     * Create an Async takes to populate the Database using the RoomDatabase Callback.
     */
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //callAsyncPopulate
            new PopulateDbAysncTask(instance).execute();
        }
    };


    /**
     * This method when called allows access to the doInBackground method call,
     * This method can populate the Room DB and allow pre made roots upon install.
     */
    private static class PopulateDbAysncTask extends AsyncTask<Void,Void,Void> {
        private RouteDao routeDao;
        PopulateDbAysncTask(RouteDatabase db) {
            routeDao = db.routeDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }


}
