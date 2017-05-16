package me.blog.cjh7163.tmaptest;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by david2 on 2017-05-14.
 */

@SuppressWarnings("deprecation")
public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder holder;
    Camera camera;

    public CameraSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
        } catch(Exception ex) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> listSize = params.getSupportedPreviewSizes();
        if (listSize == null) {
            params.setPreviewSize(w, h);
        } else {
            int diff = 10000;
            Camera.Size opti = null;
            for (Camera.Size s : listSize) {
                if (Math.abs(s.height - h) < diff) {
                    diff = Math.abs(s.height - h);
                    opti = s;
                }
            }
            params.setPreviewSize(opti.width, opti.height);
        }
        camera.setParameters(params);
        camera.setDisplayOrientation(90);
        camera.startPreview();
    }
}
