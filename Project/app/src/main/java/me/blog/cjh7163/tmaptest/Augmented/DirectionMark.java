package me.blog.cjh7163.tmaptest.Augmented;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by david2 on 2017-05-16.
 */

class DirectionMark {

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private FloatBuffer mColorBorderBuffer;
    private ByteBuffer mIndexBuffer;
    private ByteBuffer mIndexBorderBuffer;

//    private float vertices[] = {
//            // 1st floor
//            0.0f,  0.7f, 0.1f,
//            -0.4f,  0.0f, 0.1f,
//            0.4f,  0.0f, 0.1f,
//            -0.2f,  0.0f, 0.1f,
//            -0.2f, -0.5f, 0.1f,
//            0.2f, -0.5f, 0.1f,
//            0.2f,  0.0f, 0.1f,
//
//            // 2nd floor
//            0.0f,  0.7f, -0.1f,
//            -0.4f,  0.0f, -0.1f,
//            0.4f,  0.0f, -0.1f,
//            -0.2f,  0.0f, -0.1f,
//            -0.2f, -0.5f, -0.1f,
//            0.2f, -0.5f, -0.1f,
//            0.2f,  0.0f, -0.1f,
//    };
    private float vertices[] = {
            // 1st floor
            0.0f,  0.5f, 0.1f,
            -0.4f,  0.0f, 0.1f,
            0.4f,  0.0f, 0.1f,
            -0.2f,  0.0f, 0.1f,
            -0.2f, -0.5f, 0.1f,
            0.2f, -0.5f, 0.1f,
            0.2f,  0.0f, 0.1f,

            // 2nd floor
            0.0f,  0.5f, -0.1f,
            -0.4f,  0.0f, -0.1f,
            0.4f,  0.0f, -0.1f,
            -0.2f,  0.0f, -0.1f,
            -0.2f, -0.5f, -0.1f,
            0.2f, -0.5f, -0.1f,
            0.2f,  0.0f, -0.1f,
    };

    private float colors[] = {
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
            0.0f,  1.0f,  0.0f,  0.5f,
    };

    private float colorsBorder[] = {
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
            0.0f,  0.0f,  0.0f,  1.0f,
    };

    private byte indices[] = {
            // 1st floor
            0, 1, 2,
            3, 4, 5,
            3, 5, 6,

            // 2nd floor
            7, 8, 9,
            10, 11, 12,
            10, 12, 13,

            // gap
            4, 11, 12,
            4, 12, 5,

            3, 10, 11,
            3, 11, 4,

            1, 8, 10,
            1, 10, 3,

            0, 7, 8,
            0, 8, 1,

            5, 12, 13,
            5, 13, 6,

            6, 13, 9,
            6, 9, 2,

            0, 7, 9,
            0, 9, 2,
    };

    private byte indicesBorder[] = {
            // 1st floor
            0, 1,
            1, 3,
            3, 4,
            4, 5,
            5, 6,
            6, 2,
            2, 0,

            // 2nd floor
            7, 8,
            8, 10,
            10, 11,
            11, 12,
            12, 13,
            13, 9,
            9, 7,

            //gap
            0, 7,
            1, 8,
            2, 9,
            3, 10,
            4, 11,
            5, 12,
            6, 13,
    };

    public DirectionMark() {
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuf.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mColorBuffer = byteBuf.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(colorsBorder.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mColorBorderBuffer = byteBuf.asFloatBuffer();
        mColorBorderBuffer.put(colorsBorder);
        mColorBorderBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);

        mIndexBorderBuffer = ByteBuffer.allocateDirect(indicesBorder.length);
        mIndexBorderBuffer.put(indicesBorder);
        mIndexBorderBuffer.position(0);
    }

    public void draw(GL10 gl) {

        gl.glFrontFace(GL10.GL_CW);

        // Draw Shape

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        // Draw Border

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBorderBuffer);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glLineWidth(5.0f);

        gl.glDrawElements(GL10.GL_LINES, indicesBorder.length, GL10.GL_UNSIGNED_BYTE, mIndexBorderBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }

    public void setColors(float[] colors) {
        this.colors = colors;

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mColorBuffer = byteBuf.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }
}
