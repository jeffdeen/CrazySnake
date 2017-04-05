package com.example.jeffdeen.snakeproject;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.jeffdeen.snakeproject.Background.RectObject;
import com.example.jeffdeen.snakeproject.GameObjects.AI_snake;
import com.example.jeffdeen.snakeproject.GameObjects.BaseObject;
import com.example.jeffdeen.snakeproject.GameObjects.Snake;
import com.example.jeffdeen.snakeproject.Util.CParams;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jeffdeen on 2017/2/12.
 */

public class GameView extends GLSurfaceView{

    public MoveThread rthread;
    private SceneRenderer mRenderer;
    private float ratio;
    private Snake snake;
    private List<BaseObject> foods = new ArrayList<>();
    private RectObject rectObject;
    private final int foods_num = 150;
    private float[] _RMatrix			= new float[16];
    private float[] _ViewMatrix			= new float[16];
    private float[] _ProjectionMatrix	= new float[16];
    private float[] _MVPMatrix			= new float[16];
    private float zNear = 1f;
    private float zFar = 1000f;
    public int direction_flag = 0;
    //private final int bg_width = 5000;
    //private final int bg_height = 1500;
    //private List<SGenome> m_vecThePopulation = new ArrayList<>();
    private List<AI_snake> AI_snakes = new ArrayList<>();
    private List<List<BaseObject>> all_snakes = new ArrayList<>();
    private int AI_num = 10;
    private List<BaseObject> obstacles = new ArrayList<>();
    private MainActivity mainActivity;

    private boolean single_game = false;
    //private int obstacles_num = 40;
    //private int m_NumWeightsInNN;  //神经网络所有权重数量
    //private CGenAlg cGenAlg;
    //private int m_iTicks = 0;
    //private int m_iGenerations = 0;
    //private boolean isSleep = false;
    public GameView(Context context, String game) {
        super(context);
        if(game.equals("single"))
            single_game = true;
        mainActivity = (MainActivity) context;
        this.setEGLContextClientVersion(2);
        mRenderer=new SceneRenderer();
        this.setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setLengthTextView(final int length){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.setLength(""+length);
            }
        });
    }
    public void dead(final int length){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.showDeadDialog(length,GameView.this);
            }
        });
    }
//    public void setKillText(final int kill){
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainActivity.setKill(""+kill);
//            }
//        });
//    }

    private class SceneRenderer implements GLSurfaceView.Renderer{

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
            if(single_game){
                snake = new Snake(960,540,1.0f,0.0f,0.0f,GameView.this);
            }
            for(int i = 0;i<foods_num;++i){
                foods.add(new BaseObject(Random(CParams.i_bg_width),Random(CParams.i_bg_height),
                        (float)Math.random(),(float)Math.random(),(float)Math.random(),GameView.this));
            }
            for(int i =0;i<AI_num;++i){
                AI_snakes.add(new AI_snake(Random(CParams.i_bg_width),Random(CParams.i_bg_height),
                        (float)Math.random(),(float)Math.random(),(float)Math.random(),GameView.this));

            }
//            for(int i=0;i<obstacles_num;++i){
//                obstacles.add(new Obstacle(Random(CParams.i_bg_width),Random(CParams.i_bg_height),
//                        0.0f,0.0f,0.0f,GameView.this));
//            }
//            m_NumWeightsInNN = AI_snakes.get(0).GetNumberOfWeights();
//            List<Integer> SplitPoints = AI_snakes.get(0).CalculateSplitPoints();
//            cGenAlg = new CGenAlg(AI_num, CParams.dMutationRate,CParams.dCrossoverRate,m_NumWeightsInNN,SplitPoints);
//            m_vecThePopulation = cGenAlg.GetChromos();
//            for (int i=0; i<AI_num; i++)
//                AI_snakes.get(i).PutWeights(m_vecThePopulation.get(i).vecWeights);

            rectObject = new RectObject(CParams.i_bg_width, CParams.i_bg_height,GameView.this);
            rthread=new MoveThread();
            rthread.start();
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            //设置视窗大小及位置
            GLES20.glViewport(0, 0, width, height);

            //计算GLSurfaceView的宽高比
            ratio = (float) width / height;

        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            //清除颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            //if(m_iTicks++<500){
//                for(int i = 0;i<AI_num;++i){
//                    AI_snakes.get(i).Update(foods,obstacles);
//                }
//                for(int i = 0;i<AI_num;++i){
//                    for(int j=0;j<foods_num;++j){
//                        if(isCollision(AI_snakes.get(i).getHead(),foods.get(j))){
//                            //AI_snakes.get(i).addBody();
//                            //AI_snakes.get(i).IncrementFitness();
//                            foods.get(j).setCoordinate(Random(bg_width),Random(bg_height));
//                        }
//                    }
//                    for(int j = 0;j<obstacles_num;++j){
//                        if(isCollision(AI_snakes.get(i).getHead(),obstacles.get(j))){
//                            //AI_snakes.get(i).DecrementFitness();
//                            //AI_snakes.get(i).minusBody(Random(bg_width),Random(bg_height));
//                            obstacles.get(j).setCoordinate(Random(bg_width),Random(bg_height));
//                        }
//                    }

                    //m_vecThePopulation.get(i).dFitness = AI_snakes.get(i).Fitness();
 //              }

           // }
//          else{
//                m_iGenerations++;
//                m_iTicks = 0;
//                m_vecThePopulation = cGenAlg.Epoch(m_vecThePopulation);
//                for (int i=0; i<AI_num; i++){
//                    AI_snakes.get(i).PutWeights(m_vecThePopulation.get(i).vecWeights);
//                    AI_snakes.get(i).Reset(Random(bg_width),Random(bg_height));
//                }
//                Log.d("AI_Snake","generation:"+m_iGenerations);
//                Log.d("AI_Snake","best fitness:"+cGenAlg.getM_dBestFitness());
//                Log.d("AI_Snake","ava fitness:"+cGenAlg.getM_dAverageFitness());
//            }

            for(int i=0;i<AI_num;++i){
                List<BaseObject> temp_list = new ArrayList<>();
                for(int k = 0;k<AI_num;++k){
                    if(i!=k){
                        for(int j = 0;j<AI_snakes.get(k).length();++j){
                            temp_list.add(AI_snakes.get(k).snakeBodyList.get(j));
                        }
                        if(single_game){
                            for(int j=0;j<snake.length();++j){
                                temp_list.add(snake.snakeBodyList.get(j));
                            }
                        }
                    }
                }
                all_snakes.add(temp_list);
            }

            if(single_game){
                for(int i = 0;i<AI_num;++i){
                    for(int j=0;j<AI_snakes.get(i).length();++j){
                        obstacles.add(AI_snakes.get(i).snakeBodyList.get(j));
                    }
                }
                Matrix.setLookAtM(_ViewMatrix, 0,
                        snake.getSnakeGL_X(), snake.getSnakeGL_Y(), 2f,
                        snake.getSnakeGL_X(), snake.getSnakeGL_Y(), 0,
                        0, 1, 0);
                snake.Update(foods,obstacles);
                obstacles.clear();
            }else{
                Matrix.setLookAtM(_ViewMatrix, 0,
                        AI_snakes.get(0).getSnakeGL_X(), AI_snakes.get(0).getSnakeGL_Y(), 2f,
                        AI_snakes.get(0).getSnakeGL_X(), AI_snakes.get(0).getSnakeGL_Y(), 0,
                        0, 1, 0);
            }
            //draw

            Matrix.orthoM(_ProjectionMatrix, 0, -ratio , ratio, -1f, 1f , zNear, zFar);
            Matrix.setIdentityM(_RMatrix, 0);
            Matrix.multiplyMM(_MVPMatrix, 0, _ViewMatrix, 0, _RMatrix, 0);
            Matrix.multiplyMM(_MVPMatrix, 0, _ProjectionMatrix, 0, _MVPMatrix, 0);


            for(int i = 0;i<AI_num;++i){
                    AI_snakes.get(i).Update(foods,all_snakes.get(i));
            }
            all_snakes.clear();

            rectObject.draw(_MVPMatrix);
            for(int i=0;i<foods.size();++i){
                foods.get(i).draw(_MVPMatrix);
            }
            for(int i=0;i<AI_num;++i){
                AI_snakes.get(i).drawSnake(_MVPMatrix);
            }
            if(single_game){
                snake.drawSnake(_MVPMatrix);
            }

            GLES20.glFlush();
            System.gc();
        }
    }
    public class MoveThread extends Thread
    {
        public boolean flag=true;

        @Override
        public void run()
        {
            while(flag)
            {
                try
                {
                    if(single_game){
                        switch (direction_flag){
                            case 0:
                                UP();
                                break;
                            case 1:
                                Down();
                                break;
                            case 2:
                                Left();
                                break;
                            case 3:
                                Right();
                                break;
                        }
                        //snake.move(direction_flag);
                    }
                    sleep(100);
                    requestRender();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public void UP(){
        //isSleep = true;
        snake.Up();
    }
    public void Down(){
        // = false;
        snake.Down();
    }
    public void Left(){
        snake.Left();
//        File file = new File(Environment.getExternalStorageDirectory()+"/sGenome_avoid.txt");
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        SGenome sGenome = cGenAlg.getBestGenome();
//        FileOutputStream stream = null;
//        try {
//            stream = new FileOutputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        for(int i = 0;i<sGenome.vecWeights.size();++i){
//            String s = sGenome.vecWeights.get(i).toString() + "\n";
//            byte[] buf = s.getBytes();
//            try {
//                stream.write(buf);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
    public void Right(){
        snake.Right();
    }
//    private boolean isCollision(BaseObject object1,BaseObject object2){
//        int x1 = object1.getX();
//        int y1 = object1.getY();
//        int radius1 = object1.getRadius();
//        int x2 = object2.getX();
//        int y2 = object2.getY();
//        int radius2 = object2.getRadius();
//        int distance = (int)Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
//        if(distance<=(radius1+radius2)){
//            return true;
//        }
//        return false;
//    }
    private int Random(int temp){
        int value = (int)(Math.random()*temp);
        return Math.random()>0.5?value:-value;
    }
}
