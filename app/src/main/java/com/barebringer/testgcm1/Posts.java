package com.barebringer.testgcm1;


import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class Posts extends FragmentActivity implements NITpost.OnFragmentInteractionListener, STUDpost.OnFragmentInteractionListener, Fest.OnFragmentInteractionListener, Director.OnFragmentInteractionListener {

    private PagerAdapter mPagerAdapter;
    ViewPager pager;
    String username;
    SharedPreferences store;
    String temp = null;
    int f = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);

        username = store.getString("usertext", null);
        setContentView(R.layout.activity_posts);
        initialisePaging();
    }

    private void initialisePaging() {
        // TODO Auto-generated method stub
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, NITpost.class.getName()));
        fragments.add(Fragment.instantiate(this, Fest.class.getName()));
        fragments.add(Fragment.instantiate(this, Director.class.getName()));
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }

    @Override
    public String getusername3() {
        return username;
    }

    @Override
    public void logout3() {
        Intent i = new Intent(this, MainActivity.class);
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = store.edit();
        editor.putString("usertext", null);
        editor.apply();
        startActivity(i);
        return;
    }

    @Override
    public int scraper3() {
        return f;
    }

    @Override
    public String newmes3() {
        return temp;
    }

}
