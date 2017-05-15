package me.blog.cjh7163.tmaptest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

/**
 * Created by david2 on 2017-05-14.
 */

public class DirectionManager {
    private static DirectionManager instance;

    static  {
        instance = new DirectionManager();
    }

    private DirectionManager() {}

    private float[] accData = null;
    private float[] magData = null;
    private float[] rotation = new float[9];
    private float[] resultData = new float[3];

    public static DirectionManager getInstance() {
        return instance;
    }

    public SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                instance.accData = event.values.clone();
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                instance.magData = event.values.clone();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    };

    public float getAzimuth() {
        if (accData != null && magData != null) {
            SensorManager.getRotationMatrix(rotation, null, accData, magData);
            SensorManager.getOrientation(rotation, resultData);

            resultData[0] = (float)Math.toDegrees(resultData[0]);
            if(resultData[0] < 0) {
                resultData[0] += 360;
            }
            return resultData[0];
        } else {
            Log.d("Info::", "accData and magData must not be null!");
        }
        return 0;
    }

    public float getDirectionBetween(Location src, Location dest) {
        float azimuth, bearing, relative = 0.0f;

        try {
            azimuth = getAzimuth();
            bearing = src.bearingTo(dest);
            relative = bearing - azimuth;
        } catch(Exception ex) {
            Log.d("Exception::", "can not read GpsManager instance");
        }

        return relative;
    }

    public float[] getAccData() {
        return accData;
    }
    public float[] getMagData() {
        return magData;
    }
    public float[] getRotation() {
        return rotation;
    }
    public float[] getResultData() {
        return resultData;
    }

    public void setAccData(float[] accData) {
        this.accData = accData;
    }
    public void setMagData(float[] magData) {
        this.magData = magData;
    }
    public void setRotation(float[] rotation) {
        this.rotation = rotation;
    }
    public void setResultData(float[] resultData) {
        this.resultData = resultData;
    }
}
