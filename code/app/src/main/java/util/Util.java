package util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Util {


    private TimerTask routeTimerTask;
    private double routeTime = 0.0;
    private Timer routeTimer;
    private Handler handler;
    private Object Context;
    Activity activity;


    /***
     * THis class will start a new timer when called and set the view to be shown in the activity..
     * @param tView
     */
    public void startTimer( TextView tView) {
        Handler handler = new Handler(Looper.getMainLooper());
        routeTimer = new Timer();
        routeTimerTask= new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        routeTime++;
                        tView.setText(getTimerValue());
                    }
                });
            }
        };
        routeTimer.scheduleAtFixedRate(routeTimerTask, 0, 1000);
    }



    public void pauseTimer() {
        if (routeTimer != null) {
            this.routeTimer.cancel();

        }
    }



    /**
     * This Method uses the Math class to calcuatle the time.
     *
     * @return
     */
    public String getTimerValue() {

        int rounded = (int) Math.round(routeTime);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int mins = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, mins, hours);

    }

    /**
     * This method takes in three values for hours, mins and seconds
     * It then formats them in such a way to return them as timer.
     *
     * @param seconds
     * @param mins
     * @param hours
     * @return
     */
    private String formatTime(int seconds, int mins, int hours) {

        return String.format("%02d", hours) + " : " + String.format("%02d", mins) + " : " + String.format("%02d", seconds);
    }
}
