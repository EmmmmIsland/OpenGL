package com.demo.opengl.opengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.demo.opengl.R;
import com.demo.opengl.utils.LoggerConfig;
import com.demo.opengl.utils.MatrixHelper;
import com.demo.opengl.utils.ShaderHelper;
import com.demo.opengl.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    // 多少个元素是一个
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private final Context context;
    private int program;

    private static final String A_COLOR = "a_Color";
    private int aColorLocation;

//    private static final String U_COLOR = "u_Color";
//    private int uColorLocation;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    private final float[] modelMatrix = new float[16];
    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
//                // 三角形1
//                -0.5f, -0.5f,
//                0.5f, 0.5f,
//                -0.5f, 0.5f,
//                // 三角形2
//                -0.5f, -0.5f,
//                0.5f, -0.5f,
//                0.5f, 0.5f,

                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                // 直线
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                // 两个点
                0f, -0.25f, 0f, 0f, 1f,
                0f, 0.25f, 1f, 0f, 0f

        };

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);

//        uColorLocation = glGetUniformLocation(program,U_COLOR);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);

//        final float aspectRatio = width > height ?
//                (float) width / (float) height : (float) height / (float) width;
//        if (width > height) {
//            orthoM(projectionMatrix, 0, -aspectRatio , aspectRatio, -1f, 1f, -1f, 1f);
//        } else {
//            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
//        }

        MatrixHelper.perspectiveM(projectionMatrix,45,(float) width/(float) height,1f,10f);
        setIdentityM(modelMatrix,0);
        translateM(modelMatrix,0,0f,0f,-2f);


        //旋转矩阵
        translateM(modelMatrix,0,0f,0f,-2.5f);
        rotateM(modelMatrix,0,-60f,1f,0f,0f);

        // fuzhi
        final float[] temp = new float[16];
        multiplyMM(temp,0,projectionMatrix,0,modelMatrix,0);
        System.arraycopy(temp,0,projectionMatrix,0,temp.length);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0);

//        glUniform4f(uColorLocation,1.0f,1.0f,1.0f,1.0f);
        // 画三角形   0是数组开始位置  6是取6个顶点的数据（POSITION_COMPONENT_COUNT=2 所以是前12个数据） 画两个三角形
//        glDrawArrays(GL_TRIANGLES,0,6);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

//        glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f);
        // 横线 第六个开始 2组
        glDrawArrays(GL_LINES, 6, 2);

        // 画点
//        glUniform4f(uColorLocation,0.0f,0.0f,1.0f,1.0f);
        glDrawArrays(GL_POINTS, 8, 1);
//        glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
