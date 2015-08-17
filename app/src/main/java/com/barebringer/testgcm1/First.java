package com.barebringer.testgcm1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class First extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
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
        setContentView(R.layout.activity_first);
    }

    public void admin(View v) {
        Intent i = new Intent(this, Author.class);
        startActivity(i);
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
