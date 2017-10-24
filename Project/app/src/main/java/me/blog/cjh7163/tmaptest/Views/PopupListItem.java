package me.blog.cjh7163.tmaptest.Views;

/**
 * Created by david2 on 2017-10-24.
 */

public class PopupListItem {
    public String mainText;
    public String subText;
    public boolean walk;

    public PopupListItem(String mainText, String subText) {
        this(mainText, subText, false);
    }

    public PopupListItem(String mainText, String subText, boolean walk) {
        this.mainText = mainText;
        this.subText = subText;
        this.walk = walk;
    }
}
