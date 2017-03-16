package me.blog.cjh7163.tmaptest;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.util.ArrayList;

/**
 * Created by cjh71 on 2017-03-15.
 */

public class PathManager {
    private static PathManager instance;
    private PathManager() {}

    private TMapPolyLine polyLine;
    private int nearest;

    public TMapPolyLine getPolyLine() {
        return polyLine;
    }

    public void setPolyLine(TMapPolyLine polyLine) {
        this.polyLine = polyLine;
        updateNearest();
    }

    private void updateNearest() {
        nearest = - 1;
        double minVal = Double.MAX_VALUE;

        try {
            Location currentLocation = GpsManager.getInstance().getCurrentLocation();

            ArrayList<TMapPoint> points = polyLine.getLinePoint();

            for (int i=0; i<points.size(); i++) {
                TMapPoint point = points.get(i);
                double dist = Math.pow(point.getLongitude() - currentLocation.getLongitude(), 2) + Math.pow(point.getLatitude() - currentLocation.getLatitude(), 2);
//                Log.d(String.format("dist[i]:", i), ""+dist);

                if (dist < minVal) {
                    minVal = dist;
                    nearest = i;
                }
            }
        } catch(Exception ex) {
            Log.d("Exception: ", "can't calculate nearest point.");
        }
    }

    public Vector getNearestVector() {
        Vector vector = null;

        try {
            TMapPoint destinationPoint = polyLine.getLinePoint().get(nearest);

            Location currentLocation = GpsManager.getInstance().getCurrentLocation();
            Location destination = new Location(LocationManager.GPS_PROVIDER);
            destination.setLongitude(destinationPoint.getLongitude());
            destination.setLatitude(destination.getLatitude());

            vector = new Vector(currentLocation, destination);
        } catch (Exception ex) {
        }

        return vector;
    }

    public static PathManager getInstance() {
        if (instance == null) {
            instance = new PathManager();
        }
        return instance;
    }

}
