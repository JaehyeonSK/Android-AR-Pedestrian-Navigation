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
    private ArrayList<TMapPoint> points;
    private int nearest;

    public TMapPolyLine getPolyLine() {
        return polyLine;
    }

    public void setPolyLine(TMapPolyLine polyLine) {
        this.polyLine = polyLine;
        this.points = polyLine.getLinePoint();
        updateNearest();
    }

    private void updateNearest() {
        nearest = - 1;
        double minVal = Double.MAX_VALUE;

        try {
            Location currentLocation = GpsManager.getInstance().getCurrentLocation();

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
            updateNearest();
            TMapPoint destinationPoint = points.get(nearest);

            Location currentLocation = GpsManager.getInstance().getLastLocation();
            Location destination = new Location(LocationManager.GPS_PROVIDER);
            destination.setLongitude(destinationPoint.getLongitude());
            destination.setLatitude(destinationPoint.getLatitude());

            vector = new Vector(currentLocation, destination);
        } catch (Exception ex) {
            Log.d("Error", "can't get nearest vector.");
        }

        return vector;
    }

    public TMapPoint getNearestPoint() {
        TMapPoint point = null;

        try {
            updateNearest();
            point = points.get(nearest);
        } catch(Exception ex) {
            Log.d("Error::", ex.getMessage());
        }

        return point;
    }

    public boolean hasNext() {
        //points.remove(0);
        points.remove(nearest);
        return (points.size() > 0) ? true : false;
    }

    public int getNearestIndex() {
        return nearest;
    }

    public static PathManager getInstance() {
        if (instance == null) {
            instance = new PathManager();
        }
        return instance;
    }

}
