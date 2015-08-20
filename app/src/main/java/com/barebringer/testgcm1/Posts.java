package com.barebringer.testgcm1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Posts extends FragmentActivity implements NITpost.OnFragmentInteractionListener, STUDpost.OnFragmentInteractionListener {

    private PagerAdapter mPagerAdapter;
    GoogleCloudMessaging gcm;
    ViewPager pager;
    String username;
    SharedPreferences store;
    String temp = null;
    int f = 1;

    Runnable r = new Runnable() {
        @Override
        public void run() {
            while (f == 1) {
                String t = store.getString("temp", null);
                if (t == null) continue;
                else if (temp == null) {
                    temp = t;
                } else if (!t.equals(temp)) {
                    temp = t;
                }
            }
        }
    };
    Thread t = new Thread(r);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        AudioManager m_amAudioManager;
        m_amAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        m_amAudioManager.setMode(AudioManager.MODE_IN_CALL);
        m_amAudioManager.setSpeakerphoneOn(false);

        username = store.getString("usertext", null);
        setContentView(R.layout.activity_posts);
        initialisePaging();
        t.start();
    }

    private void initialisePaging() {
        // TODO Auto-generated method stub
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, NITpost.class.getName()));
        fragments.add(Fragment.instantiate(this, STUDpost.class.getName()));
        mPagerAdapter = new PagerAdapter(this.getSupportFragmentManager(), fragments);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(mPagerAdapter);
    }


    @Override
    public void logout1() {
        Intent i = new Intent(this, MainActivity.class);
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = store.edit();
        editor.putString("usertext", null);
        editor.apply();
        startActivity(i);
        return;
    }

    @Override
    public int scraper1() {
        return f;
    }

    @Override
    public String newmes() {
        return temp;
    }

    @Override
    public void logout2() {
        Intent i = new Intent(this, MainActivity.class);
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = store.edit();
        editor.putString("usertext", null);
        editor.apply();
        startActivity(i);
        return;
    }


    @Override
    public String getusername1() {
        return username;
    }

    @Override
    public String getusername2() {
        return username;
    }

    @Override
    protected void onStop() {
        super.onStop();
        f = 0;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        f = 0;
        finish();
    }

}
