package com.delta.campuscomm;


import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

public class AllFunctionsActivity extends ActionBarActivity implements ActionBar.TabListener,
        SendPostsFragment.OnFragmentInteractionListener, ViewAllPostsFragment.OnFragmentInteractionListener,
        ViewDirPostsFragment.OnFragmentInteractionListener,ViewFestPostsFragment.OnFragmentInteractionListener {

    private PagerAdapter pagerAdapter;
    ActionBar actionBar;
    ViewPager pager;
    String username;
    SharedPreferences store;
    JSONObject tagsJSON=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get username from shared mem
        store = getSharedPreferences("campuscomm", Context.MODE_PRIVATE);
        username = store.getString("userName", null);
        setContentView(R.layout.activity_all_functions);

        //initialise paging
        List<Fragment> fragments = new Vector<Fragment>();
        ViewAllPostsFragment viewAllPostsFragment = (ViewAllPostsFragment)Fragment.instantiate(this, ViewAllPostsFragment.class.getName());
        viewAllPostsFragment.displayPosts(tagsJSON);
        ViewDirPostsFragment viewDirPostsFragment = (ViewDirPostsFragment)Fragment.instantiate(this, ViewDirPostsFragment.class.getName());
        viewDirPostsFragment.displayPosts(tagsJSON);
        ViewFestPostsFragment viewFestPostsFragment = (ViewFestPostsFragment)Fragment.instantiate(this, ViewFestPostsFragment.class.getName());
        viewFestPostsFragment.displayPosts(tagsJSON);
        fragments.add(viewAllPostsFragment);
        fragments.add(viewDirPostsFragment);
        fragments.add(viewFestPostsFragment);
        fragments.add(Fragment.instantiate(this, SendPostsFragment.class.getName()));
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
        db.limitTabletoN(100);
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
    public String getUserNameSendPostsFragment() {
        return username;
    }

    @Override
    public String getUserNameViewAllPostsFragment() {
        return username;
    }

    @Override
    public String getUserNameViewFestPostsFragment() {
        return username;
    }

    @Override
    public String getUserNameViewDirPostsFragment() {
        return username;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_functions, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(this,FilterActivity.class);
                startActivityForResult(intent,1);
                return true;
            case R.id.item2:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.onCreate(null);
        try {
            Log.d("tagsJSON",data.getStringExtra("tagsJSON"));
            tagsJSON = new JSONObject(data.getStringExtra("tagsJSON"));
        }catch (Exception e) {
            Log.d(CommonUtilities.TAG,e+"");
        }
    }

}
