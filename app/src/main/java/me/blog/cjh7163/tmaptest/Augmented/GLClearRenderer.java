package me.blog.cjh7163.tmaptest.Augmented;

/**
 * Created by david2 on 2017-05-16.
 */


import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.blog.cjh7163.tmaptest.Settings.Preference;

public class GLClearRenderer implements Renderer {
    private Preference preference;

    private DirectionMark directionMark;
    private float angleX, angleY, angleZ;
    private float x = 0.0f, y = 0.0f;

    public GLClearRenderer() {
        try {
            directionMark = new DirectionMark();
            preference = Preference.getInstance();

            int idx = 0;
            float[] colors = new float[80];
            for (int i = 0; i < 20; i++) {
                colors[idx] = preference.color.R;
                colors[idx + 1] = preference.color.G;
                colors[idx + 2] = preference.color.B;
                colors[idx + 3] = preference.directionMarkOpacity;
                idx += 4;
            }

            directionMark.setColors(colors);
        } catch(Exception ex) {
            Log.d("Exception in Renderer:", ex.getMessage());
        }
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(x, y, -10.0f);
//        gl.glScalef(2.0f, 2.0f, 2.0f);
        gl.glScalef(1.5f, 1.5f, 1.5f);

        gl.glRotatef(angleX, 1.0f, 0f, 0f);
        gl.glRotatef(angleY, 0f, 1.0f, 0f);
        gl.glRotatef(angleZ, 0f, 0f, 1.0f);

        directionMark.draw(gl);

        gl.glLoadIdentity();
    }


    public void onSurfaceChanged( GL10 gl, int width, int height ) {
        // This is called whenever the dimensions of the surface have changed.
        // We need to adapt this change for the GL viewport.
        gl.glViewport( 0, 0, width, height );
        //gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getAngleX() {
        return angleX;
    }

    public void setAngleX(float angleX) {
        this.angleX = angleX;
    }

    public float getAngleY() {
        return angleY;
    }

    public void setAngleY(float angle) {
        angleY = angle;
    }

    public float getAngleZ() {
        return angleZ;
    }

    public void setAngleZ(float angleZ) {
        this.angleZ = angleZ;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config ) {
        // No need to do anything here.
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST);
    }
}