package com.example.jeffdeen.snakeproject.Background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.jeffdeen.snakeproject.GameView;
import com.example.jeffdeen.snakeproject.R;
import com.example.jeffdeen.snakeproject.Util.ShaderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


/**
 * Created by jeffdeen on 2017/2/14.
 */

public class RectObject {
    private int widht;
    private int height;
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;
    private FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    private float gl_x;
    private float gl_y;

    private int mProgram;//自定义渲染管线程序id
    private int muMVPMatrixHandle;//总变换矩阵引用id
    private int maPositionHandle; //顶点位置属性引用id
    private int maTexCoordHandle; //顶点纹理坐标属性引用id
    private int mSamplerHandle; //顶点纹理
    private int _textureId;

    private GameView _context;


    public RectObject(int width, int height, GameView mv) {
        this.widht = width;
        this.height = height;
        _context = mv;
        initShader(mv);
        initVertexData();
    }
    private void initVertexData()
    {

        gl_x = widht/(1920/2.0f);
        gl_y = height/(1080/2.0f);
        float[] vertices=
                {
                        -gl_x,gl_y,-1f,
                        -gl_x,-gl_y,-1f,
                        gl_x,gl_y,-1f,
                        gl_x,-gl_y,-1f
                };
        short[] index = {
                0,1,2,1,2,3
        };

        //顶点纹理坐标数据的初始化================begin============================
        float[] texCoord=//顶点颜色值数组，每个顶点4个色彩值RGBA
                {
                        0.0f,0.0f,
                        0.0f,(int)(height/32*1.778),
                        widht/32,0.0f,
                        widht/32,(int)(height/32*1.778),
                };

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        _textureId = textures[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureId);
        InputStream is1 = _context.getResources().openRawResource(R.drawable.map);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is1);
        } finally {
            try {
                is1.close();
            } catch(IOException e) {
                //e.printStackTrace();
            }
        }
        //GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); // GL_LINEAR
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片

        ByteBuffer VBB = ByteBuffer.allocateDirect(vertices.length * 4);
        VBB.order(ByteOrder.nativeOrder());
        mVertexBuffer = VBB.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer IBB = ByteBuffer.allocateDirect(index.length * 2);
        IBB.order(ByteOrder.nativeOrder());
        mIndexBuffer = IBB.asShortBuffer();
        mIndexBuffer.put(index);
        mIndexBuffer.position(0);

        //创建顶点纹理坐标数据缓冲
        ByteBuffer CBB = ByteBuffer.allocateDirect(texCoord.length*4);
        CBB.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = CBB.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoord);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }


    //初始化shader
    public void initShader(GameView mv)
    {
        //加载顶点着色器的脚本内容
        String mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_texture.sh", mv.getResources());
        //加载片元着色器的脚本内容
        String mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_texture.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取顶点纹理坐标属性id
        maTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");

    }

    public void draw(float[] _MVPMatrix){
        GLES20.glUseProgram(mProgram);

        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 12,mVertexBuffer );
        GLES20.glEnableVertexAttribArray(maPositionHandle);

        GLES20.glVertexAttribPointer(maTexCoordHandle, 2, GLES20.GL_FLOAT, false, 8, mTexCoorBuffer);
        GLES20.glEnableVertexAttribArray(maTexCoordHandle);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, _MVPMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureId);
        GLES20.glUniform1i(mSamplerHandle, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

    }

}
