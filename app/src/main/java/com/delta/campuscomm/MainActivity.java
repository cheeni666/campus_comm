package com.delta.campuscomm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import static com.delta.campuscomm.CommonUtilities.*;

public class MainActivity extends AppCompatActivity {

    Button butonEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butonEnter = (Button)findViewById(R.id.button_enter_main);

        SharedPreferences store = getSharedPreferences("campuscomm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = store.edit();
        editor.apply();

        //to verify that app is running
        isAppRun = true;

        myDBHandler = new MyDBHandler(getApplicationContext(), null, null, 1);

        //Check if user already logged in and  if yes, go to posts activity
        if (store.getString("userName", null) != null) {
            Intent i = new Intent(this, AllFunctionsActivity.class);
            finish();
            startActivity(i);
            return;
        }

        butonEnter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int intEvent = event.getAction();

                if(intEvent == MotionEvent.ACTION_DOWN)
                    butonEnter.setBackgroundResource(R.drawable.round_bg_white);

                else if(intEvent == MotionEvent.ACTION_UP){
                    butonEnter.setBackgroundResource(R.drawable.round_bg_blue);
                    Intent intent = new Intent(MainActivity.this, AuthorActivity.class);
                    startActivity(intent);
                    finish();
                }

                return true;
            }
        });

    }

}
