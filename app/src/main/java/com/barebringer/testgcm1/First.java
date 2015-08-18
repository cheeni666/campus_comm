package com.barebringer.testgcm1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class First extends Activity {

    ImageView gif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        gif=(ImageView)findViewById(R.id.imagegif);
        gif.setBackgroundResource( R.drawable.animator);
        AnimationDrawable anim = (AnimationDrawable)gif.getBackground();
        anim.start();
        if (store.getString("usertext", null) != null) {
            Intent i = new Intent(this, Posts.class);
            startActivity(i);
            return;
        }
        if (getIntent().getStringExtra("username") != null) {
            Intent i = new Intent(getApplicationContext(), Posts.class);
            startActivity(i);
            return;
        }
    }

    public void user(View v) {
        Intent i = new Intent(this, Author.class);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
