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

    TranslateAnimation g1,g2;
    ImageView gif1,gif2;
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
        WindowManager wm=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display display=wm.getDefaultDisplay();
        float width=display.getWidth();

        start1 = true;
        start2 = true;
        start3 = true;
        apprun = true;
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);

        gif1=(ImageView)findViewById(R.id.imagegif1);
        gif2=(ImageView)findViewById(R.id.imagegif2);
        g1=new TranslateAnimation(0,-width/2-25,0,0);
        g2=new TranslateAnimation(0,width/2,0,0);
        g1.setFillAfter(true);
        g2.setFillAfter(true);
        g1.setDuration(2000);
        g2.setDuration(2000);
        SharedPreferences.Editor editor = store.edit();
        editor.putString("temp", null);
        String tag = "STUDENT-/BTECH-/2-/CSE-/";
        editor.putString("defTags", tag);
        editor.apply();
        if (store.getString("usertext", null) != null) {
            Intent i = new Intent(this, Posts.class);
            finish();
            startActivity(i);
            return;
        }

    }

    public void enter(View v) {
        gif1.startAnimation(g1);
        gif2.startAnimation(g2);
        t.start();
    }
}
