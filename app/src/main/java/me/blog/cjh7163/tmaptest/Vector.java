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

            direction = 0.0;
            distance = 0.0;
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
