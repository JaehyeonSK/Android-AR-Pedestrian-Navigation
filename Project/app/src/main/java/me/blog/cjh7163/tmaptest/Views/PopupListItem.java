package me.blog.cjh7163.tmaptest.Views;

import com.skp.Tmap.TMapPoint;

/**
 * Created by david2 on 2017-10-24.
 */

public class PopupListItem {
    public String mainText;
    public String subText;
    public boolean walk;
    public TMapPoint[] points;

    public PopupListItem(String mainText, String subText) {
        this(mainText, subText, false);
    }

    public PopupListItem(String mainText, String subText, boolean walk) {
        this(mainText, subText, walk, null);
    }

    public PopupListItem(String mainText, String subText, boolean walk, TMapPoint[] mapPoints) {
        this.mainText = mainText;
        this.subText = subText;
        this.walk = walk;
        this.points = mapPoints;
    }
}
