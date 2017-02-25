package com.example.jeffdeen.snakeproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {
    private Button single_bt,ai_bt,more_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        single_bt = (Button) findViewById(R.id.single);
        ai_bt = (Button) findViewById(R.id.ai);
        single_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                intent.putExtra("game","single");
                startActivity(intent);
            }
        });
        ai_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                intent.putExtra("game","ai");
                startActivity(intent);
            }
        });
    }
}
