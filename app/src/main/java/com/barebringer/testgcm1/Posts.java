package com.barebringer.testgcm1;


import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class Posts extends ActionBarActivity implements ActionBar.TabListener,MessageFragment.OnFragmentInteractionListener, NITpost.OnFragmentInteractionListener, STUDpost.OnFragmentInteractionListener, Fest.OnFragmentInteractionListener, Director.OnFragmentInteractionListener {

    private PagerAdapter mPagerAdapter;
    ActionBar ab;
    ViewPager pager;
    String username;
    SharedPreferences store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        username = store.getString("usertext", null);
        setContentView(R.layout.activity_posts);
        //init paging
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, NITpost.class.getName()));
        fragments.add(Fragment.instantiate(this, Fest.class.getName()));
        fragments.add(Fragment.instantiate(this, Director.class.getName()));
        fragments.add(Fragment.instantiate(this, MessageFragment.class.getName()));
        mPagerAdapter = new PagerAdapter(this.getSupportFragmentManager(), fragments);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(mPagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ab.selectTab(ab.getTabAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ab = getSupportActionBar();
        ab.setTitle(username);
        ab.setDisplayShowTitleEnabled(true);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.addTab(ab.newTab().setTabListener(this).setText("ALL POSTS"));
        ab.addTab(ab.newTab().setTabListener(this).setText("FESTS"));
        ab.addTab(ab.newTab().setTabListener(this).setText("DIRECTOR"));
        ab.addTab(ab.newTab().setTabListener(this).setText("POST"));
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyDBHandler db = new MyDBHandler(this, null, null, 1);
        SQLiteDatabase dj = db.getDB();
        String query = "SELECT * FROM " + "posts" + " WHERE 1 ORDER BY " + "_id" + " DESC;";
        Cursor c = dj.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();
        int lat_id = 0;
        if (c.getCount() != 0) {
            lat_id = c.getInt(c.getColumnIndex("_id"));
        }
        lat_id -= 20;
        lat_id++;
        query = "DELETE FROM " + "posts" + " WHERE _id < " + lat_id + ";";
        dj.execSQL(query);

        query = "SELECT * FROM " + "fposts" + " WHERE 1 ORDER BY " + "_id" + " DESC;";
        c = dj.rawQuery(query, null);
        c.moveToFirst();
        lat_id = 0;
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("post")) != null) {
                lat_id++;
                if (lat_id == 20) {
                    lat_id = c.getInt(c.getColumnIndex("_id"));
                    query = "DELETE FROM " + "fposts" + " WHERE _id < " + lat_id + ";";
                    dj.execSQL(query);
                    break;
                }
            }
            c.moveToNext();
        }

        query = "SELECT * FROM " + "dposts" + " WHERE 1 ORDER BY " + "_id" + " DESC;";
        c = dj.rawQuery(query, null);
        c.moveToFirst();
        lat_id = 0;
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("post")) != null) {
                lat_id++;
                if (lat_id == 20) {
                    lat_id = c.getInt(c.getColumnIndex("_id"));
                    query = "DELETE FROM " + "dposts" + " WHERE _id < " + lat_id + ";";
                    dj.execSQL(query);
                    break;
                }
            }
            c.moveToNext();
        }
        dj.close();
        db.close();
        finish();
    }

    @Override
    public String getusername3() {
        return username;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public String getusernamemes() {
        return username;
    }
}
