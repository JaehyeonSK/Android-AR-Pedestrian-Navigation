package me.blog.cjh7163.tmaptest;

import android.location.Location;

/**
 * Created by david2 on 2017-05-16.
 */

public class LocationUtils {
    public static float directionBetween(Location src, Location dest) {
        return DirectionManager.getInstance().getDirectionBetween(src, dest);
    }

    public static float distanceBetween(Location src, Location dest) {
        return src.distanceTo(dest);
    }
}
