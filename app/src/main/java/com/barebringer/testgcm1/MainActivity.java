package com.barebringer.testgcm1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import static com.barebringer.testgcm1.CommonUtilities.SERVER_URL;
import static com.barebringer.testgcm1.CommonUtilities.TAG;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        gif = (ImageView) findViewById(R.id.imagegif);
        gif.setBackgroundResource(R.drawable.animator);
        AnimationDrawable anim = (AnimationDrawable) gif.getBackground();
        anim.start();
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
        Intent i = new Intent(this, Author.class);
        startActivity(i);
    }
}
