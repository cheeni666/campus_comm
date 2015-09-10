package com.barebringer.testgcm1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import static com.barebringer.testgcm1.CommonUtilities.apprun;
import static com.barebringer.testgcm1.CommonUtilities.start1;
import static com.barebringer.testgcm1.CommonUtilities.start2;
import static com.barebringer.testgcm1.CommonUtilities.start3;

public class MainActivity extends AppCompatActivity {

    AnimationDrawable anim;
    ImageView gif;
    Runnable r = new Runnable() {
        @Override
        public void run() {
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() < time + 1200) ;
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
        start1 = true;
        start2 = true;
        start3 = true;
        apprun = true;
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        gif = (ImageView) findViewById(R.id.imagegif);
        gif.setBackgroundResource(R.drawable.animator);
        anim = (AnimationDrawable) gif.getBackground();
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
        anim.start();
        t.start();
    }
}
