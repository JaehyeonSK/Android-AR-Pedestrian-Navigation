package me.blog.cjh7163.tmaptest.Settings;

/**
 * Created by cjh71 on 2017-05-19.
 */

public class Preference {
    public float directionMarkOpacity;
    public boolean isStabilize;
    public boolean isCorrect;
    public Color color;

    private static Preference instance;

    public static Preference getInstance() {
        if(instance == null) {
            instance = new Preference();
        }
        return instance;
    }

    public Preference() {
        directionMarkOpacity = 0.5f;

        isStabilize = false;
        isCorrect = false;

        color = new Color();
        color.R = 0.0f;
        color.G = 1.0f;
        color.B = 0.0f;
    }

    public static class Color {
        public float R, G, B;
    }
}
