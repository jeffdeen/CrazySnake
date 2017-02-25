package com.example.jeffdeen.snakeproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jeffdeen.snakeproject.Util.BackgroundMusic;

public class MainActivity extends Activity {

    private GameView gameView;
    private TextView length_txt;//kill_txt;

    private float x;
    private float y;
    private float z;
    BackgroundMusic backgroundMusic = BackgroundMusic.getInstance(MainActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent  = getIntent();
        String game = intent.getStringExtra("game");
        gameView=new GameView(this,game);
        gameView.requestFocus();
        gameView.setFocusableInTouchMode(true);
        setContentView(gameView);


        backgroundMusic.playBackgroundMusic("bg.mp3",true);
        if(game.equals("single")){
            RelativeLayout layout = new RelativeLayout(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );

            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layoutView  = inflater.inflate(R.layout.up_down,layout,false);
            layout.addView(layoutView);
            addContentView(layout, layoutParams);
            Button buttonUp, buttonDown, buttonLeft, buttonRight;
            buttonUp = (Button) findViewById(R.id.up);
            buttonDown = (Button) findViewById(R.id.down);
            buttonLeft = (Button) findViewById(R.id.left);
            buttonRight = (Button) findViewById(R.id.right);
            buttonUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gameView.direction_flag = 0;
                }
            });
            buttonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gameView.direction_flag = 3;
                }
            });
            buttonLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gameView.direction_flag = 2;
                }
            });
            buttonDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gameView.direction_flag = 1;
                }
            });
            length_txt = (TextView)findViewById(R.id.length);
            //kill_txt = (TextView)findViewById(R.id.kill);
        }
//        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sm.registerListener(new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent sensorEvent) {
//                if (Sensor.TYPE_ACCELEROMETER != sensorEvent.sensor.getType()) {
//                    return;
//                }
//                float[] values = sensorEvent.values;
//                x = values[0];
//                y = values[1];
//                z = values[2];
//                oritationListener();
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int i) {
//
//            }
//        },sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void setLength(String length){
        length_txt.setText(length);
    }
//    public void setKill(String kill){
//        kill_txt.setText(kill);
//    }
    public void oritationListener(){
        //上
        if(x<0 && x<y){
            gameView.direction_flag = 0;
        }else if(x > 0 && x > y){
            gameView.direction_flag = 1;
        }else if(y<0 && y<x){
            gameView.direction_flag = 2;
        }else if(y>x && y>0){
            gameView.direction_flag = 3;
        }
    }
    public void setUpDownClickListeners() {

//        buttonUp.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                gameView.UP();
//                //gameView.direction_flag = 0;
//                return false;
//            }
//        });
//
//        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                gameView.Left();
//                //gameView.direction_flag = 2;
//                return false;
//            }
//        });
//        buttonRight.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                gameView.Right();
//                //gameView.direction_flag = 3;
//                return false;
//            }
//        });
//        buttonDown.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                //gameView.direction_flag = 1;
//                gameView.Down();
//                return false;
//            }
//        });
    }
    @Override
    public void onResume()
    {
        super.onResume();
        gameView.onResume();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        gameView.onPause();
        backgroundMusic.end();
    }

    public void showDeadDialog(int length, final GameView gameView){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("You are Dead!!!");
        dialog.setMessage("长度: "+ length);
        backgroundMusic.pauseBackgroundMusic();
        dialog.setPositiveButton("重来", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                gameView.rthread.flag = true;
                gameView.rthread.start();
                backgroundMusic.resumeBackgroundMusic();
            }
        });
        dialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.show();
    }
}
