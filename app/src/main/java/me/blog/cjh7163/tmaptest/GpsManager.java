package me.blog.cjh7163.tmaptest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by cjh71 on 2017-03-15.
 */

public class GpsManager {
    private static final int REQUEST_LOCATION = 0x123456;
    private static boolean init = false;

    private static GpsManager instance;

    private LocationManager locManager;
    private Activity appContext;

    private GpsManager() {}


    public static GpsManager getInstance() throws Exception {
        if (!init) {
            throw new Exception("you must initialize before using gps manager.");
        }
        return instance;
    }

    public static void init(Context context) {
        if (!init) {
            try {
                instance = new GpsManager();

                instance.locManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                instance.appContext = (Activity)context;

                init = true;
            } catch (Exception ex) {
                Log.d("Exception: ", "Failed to initialize GPS");
            }
        }
    }

    public void setOnLocationListener(LocationListener locationListener) {
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(appContext, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    public Location getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(appContext, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d("current: ", location.getLongitude() + ", " + location.getLatitude());
        return location;
    }
}
