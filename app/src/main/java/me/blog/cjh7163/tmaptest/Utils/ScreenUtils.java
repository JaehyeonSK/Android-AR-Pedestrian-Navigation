package me.blog.cjh7163.tmaptest.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by david2 on 2017-05-30.
 */

public class ScreenUtils {
    public static float getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        Activity activity = (Activity)context;
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        return dm.heightPixels;
    }

    public static float getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        Activity activity = (Activity)context;
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        return dm.widthPixels;
    }
}
