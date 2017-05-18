package me.blog.cjh7163.tmaptest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Iterator;

/**
 * Created by cjh71 on 2017-03-15.
 */

@SuppressWarnings("deprecation")
public class GpsManager {
    private static final int REQUEST_LOCATION = 0x123456;
    private static boolean init = false;

    private static GpsManager instance;

    private LocationManager locManager;
    private Activity appContext;

    private Location lastLocation;

    private GpsManager() {}

    private int getGpsSatelliteCount() {
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(appContext, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        LocationManager lm = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
        final GpsStatus gs =  lm.getGpsStatus(null);

        int i = 0, j = 0;
        final Iterator<GpsSatellite> iter = gs.getSatellites().iterator();

        while(iter.hasNext()) {
            GpsSatellite satellite = iter.next();

            if (satellite.usedInFix()) {
                j++;
            }
            i++;
        }
        return j;
    }

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
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, locationListener);
    }

    public Location getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(appContext, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        // TODO: fly gps 테스트 전용
//        return locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Location location;
        int satelliteCount = getGpsSatelliteCount();

        if(satelliteCount < 4) {
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

//        Log.d("current: ", location.getLongitude() + ", " + location.getLatitude());

        if (isBetterLocation(location, lastLocation)) {
            this.lastLocation = location;
            return location;
        } else {
            return lastLocation;
        }

    }

    public Location getLastLocation() {
        if(lastLocation == null) {
            lastLocation = getCurrentLocation();
        }
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    private static final int TWO_SECONDS = 1000 * 60;

    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_SECONDS;
        boolean isSignificantlyOlder = timeDelta < -TWO_SECONDS;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }

        return false;
    }

    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
