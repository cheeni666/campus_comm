package com.barebringer.testgcm1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import static com.barebringer.testgcm1.CommonUtilities.apprun;
import static com.barebringer.testgcm1.CommonUtilities.start1;
import static com.barebringer.testgcm1.CommonUtilities.start2;
import static com.barebringer.testgcm1.CommonUtilities.start3;

public class MainActivity extends AppCompatActivity {

    TranslateAnimation leftgate_anim, rightgate_anim;
    ImageView leftgate_image, rightgate_image;

    Runnable r = new Runnable() {
        @Override
        public void run() {
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() < time + 2250) ;
            Intent i = new Intent(MainActivity.this, Author.class);
            finish();
            startActivity(i);
        }
    };
    Thread t = new Thread(r);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        float width = display.getWidth();

        //Explained in common utilities
        start1 = true;
        start2 = true;
        start3 = true;
        apprun = true;

        leftgate_image = (ImageView) findViewById(R.id.first_leftgate_image);
        leftgate_anim = new TranslateAnimation(0, -width / 2 - 25, 0, 0);
        leftgate_anim.setFillAfter(true);
        leftgate_anim.setDuration(2000);

        rightgate_image = (ImageView) findViewById(R.id.first_rightgate_image);
        rightgate_anim = new TranslateAnimation(0, width / 2, 0, 0);
        rightgate_anim.setFillAfter(true);
        rightgate_anim.setDuration(2000);

        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = store.edit();
        editor.apply();

        //Check if user already logged in and  if yes, go to posts activity
        if (store.getString("username", null) != null) {
            Intent i = new Intent(this, Posts.class);
            finish();
            startActivity(i);
            return;
        }

    }

    public void enter(View v) {
        leftgate_image.startAnimation(leftgate_anim);
        rightgate_image.startAnimation(rightgate_anim);

        //Thread to wait for animation to complete and go to auuthor activity
        t.start();
    }
}
