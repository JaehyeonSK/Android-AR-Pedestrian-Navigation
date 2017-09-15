package me.blog.cjh7163.tmaptest;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by cjh71 on 2017-03-15.
 */

public class Vector {
    private Location start, end;
    private double direction;
    private double distance;

    public Vector(@NonNull Location start, @NonNull Location end) {
        this.start = start;
        this.end = end;

        try {
            // 각도 구하기
//            double p, q;
//            p = end.getLongitude() - start.getLongitude(); // 경도 차
//            q = end.getLatitude() - start.getLatitude(); // 위도 차
//
//            Log.d("end.long:", ""+end.getLongitude());
//            Log.d("end.lat:", ""+end.getLatitude());
//            Log.d("start.long:", ""+start.getLongitude());
//            Log.d("start.lat:", ""+start.getLatitude());
//            Log.d("p:", ""+p);
//            Log.d("q:", ""+q);
//
//            direction = Math.toDegrees(Math.atan(Math.sin(p/q)));

            float[] results = new float[3];
            Location.distanceBetween(
                    start.getLatitude(), start.getLongitude(),
                    end.getLatitude(), end.getLongitude(), results
            );
            distance = results[0];
            direction = DirectionManager.getInstance().getDirectionBetween(start, end);

            Log.d("== Vector Result ==", "");
            Log.d("results(distance)::", String.valueOf(distance));
            Log.d("results(direction)::", String.valueOf(direction));
            Log.d("===================", "");
        } catch (Exception ex) {
            Log.d("Exception: ", "can't calculate vector.");
        }
    }

    public double getDirection() {
        return direction;
    }

    public double getDistance() {
        return distance;
    }
}
