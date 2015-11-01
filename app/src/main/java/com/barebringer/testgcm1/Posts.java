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

public class Posts extends ActionBarActivity implements ActionBar.TabListener,
        MessageFragment.OnFragmentInteractionListener, NITpost.OnFragmentInteractionListener,
        Fest.OnFragmentInteractionListener, Director.OnFragmentInteractionListener {

    private PagerAdapter pagerAdapter;
    ActionBar actionBar;
    ViewPager pager;
    String username;
    SharedPreferences store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get username from shared mem
        store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        username = store.getString("username", null);
        setContentView(R.layout.activity_posts);

        //initialise paging
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, NITpost.class.getName()));
        fragments.add(Fragment.instantiate(this, Fest.class.getName()));
        fragments.add(Fragment.instantiate(this, Director.class.getName()));
        fragments.add(Fragment.instantiate(this, MessageFragment.class.getName()));
        pagerAdapter = new PagerAdapter(this.getSupportFragmentManager(), fragments);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(4);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.selectTab(actionBar.getTabAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //customising actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(username);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setTabListener(this).setText("ALL POSTS"));
        actionBar.addTab(actionBar.newTab().setTabListener(this).setText("FESTS"));
        actionBar.addTab(actionBar.newTab().setTabListener(this).setText("DIRECTOR"));
        actionBar.addTab(actionBar.newTab().setTabListener(this).setText("POST"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyDBHandler db = new MyDBHandler(this, null, null, 1);
        db.limitTabletoN("posts", 20);
        db.limitTabletoN("fposts", 20);
        db.limitTabletoN("dposts", 20);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
    public String getusername_message() {
        return username;
    }

    @Override
    public String getusername_nitpost() {
        return username;
    }

    @Override
    public String getusername_fest() {
        return username;
    }

    @Override
    public String getusername_director() {
        return username;
    }
}
