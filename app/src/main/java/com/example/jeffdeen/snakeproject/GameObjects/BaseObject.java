package com.example.jeffdeen.snakeproject.GameObjects;

import android.opengl.GLES20;

import com.example.jeffdeen.snakeproject.GameView;
import com.example.jeffdeen.snakeproject.Util.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * Created by jeffdeen on 2017/2/12.
 */

public class BaseObject {
    private float x;
    private float y;
    private int radius = 15;
    private FloatBuffer mVertexBuffer;
    private float[] color = new float[3];
    private static final int FAN_NUM = 50;
    private float gl_x;
    private float gl_y;
    private float[] vertices = new float[FAN_NUM*3];

    private int mProgram;//自定义渲染管线程序id
    private int muMVPMatrixHandle;//总变换矩阵引用id
    private int maPositionHandle; //顶点位置属性引用id
    private  int maColorHandle; //顶点颜色属性引用id


    public BaseObject(float x, float y, float r, float g, float b, GameView mv) {
        this.x = x;
        this.y = y;
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        initShader(mv);
    }
    private void initVertexData()
    {
        gl_x = x/(1920/2.0f);
        gl_y = y/(1080/2.0f);
        float gl_radius = this.radius/(1920/2.0f);
        for(int i=0;i<FAN_NUM;++i){
            float triangle_fan_x = (float) (gl_x + gl_radius * Math.cos(2*Math.PI*i/FAN_NUM));
            float triangle_fan_y = (float) (gl_y + gl_radius * Math.sin(2*Math.PI*i/FAN_NUM));
            vertices[i*3] = triangle_fan_x;
            vertices[i*3+1] = triangle_fan_y;
            vertices[i*3+2] = 0.0f;
        }
        ByteBuffer VBB = ByteBuffer.allocateDirect(vertices.length * 4);
        VBB.order(ByteOrder.nativeOrder());
        mVertexBuffer = VBB.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

    }


    //初始化shader
    private void initShader(GameView mv)
    {
        //加载顶点着色器的脚本内容
        String mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        String mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id
        maColorHandle= GLES20.glGetUniformLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void draw(float[] _MVPMatrix){
        initVertexData();
        GLES20.glUseProgram(mProgram);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 12,mVertexBuffer );
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glUniform3f(maColorHandle, color[0],
                color[1],color[2]);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, _MVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,FAN_NUM);

    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public void setCoordinate(float x, float y){
        this.x = x;
        this.y = y;
    }
    public float[] getColor() {return color;}
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getGl_x() {
        return gl_x;
    }

    public float getGl_y() {
        return gl_y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

}
