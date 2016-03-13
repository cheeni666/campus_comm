package com.delta.campuscomm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {

    Calendar calendar;
    int cy;
    int level=1;
    ArrayAdapter adapter;
    ArrayList<String> level1 = new ArrayList<String>(Arrays.asList("cse", "ece", "eee", "mech", "chem", "prod", "ice", "civil", "meta", "archi"));
    ArrayList<String> level2 = new ArrayList<String>();
    ArrayList<String> level3 = new ArrayList<String>(Arrays.asList("btech", "mtech", "other"));
    ArrayList<Boolean> stateLevel1 = new ArrayList<>();
    ArrayList<Boolean> stateLevel2 = new ArrayList<>();
    ArrayList<Boolean> stateLevel3 = new ArrayList<>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView)findViewById(R.id.filter_tags_list);
        adapter = new ListAdapter(this,R.layout.adapter_list,level1,stateLevel1);
        listView.setAdapter(adapter);
        for(int i=0;i<10;i++)
            stateLevel1.add(false);
        for(int i=0;i<5;i++)
            stateLevel2.add(false);
        for(int i=0;i<3;i++)
            stateLevel3.add(false);
        calendar = Calendar.getInstance();
        cy = calendar.get(Calendar.YEAR);
        cy = cy % 100;
        level2 = new ArrayList<String>();
        level2.add("" + cy);
        cy--;
        level2.add("" + cy);
        cy--;
        level2.add("" + cy);
        cy--;
        level2.add("" + cy);
        cy--;
        level2.add("" + cy);
        final FloatingActionButton fabPrev = (FloatingActionButton) findViewById(R.id.fabPrev);
        final FloatingActionButton fabNext = (FloatingActionButton) findViewById(R.id.fabNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (level == 1) {
                    adapter = new ListAdapter(FilterActivity.this,R.layout.adapter_list,level2,stateLevel2);
                    fabPrev.setVisibility(View.VISIBLE);
                    level++;
                    listView.setAdapter(adapter);
                }
                else if(level == 2) {
                    adapter = new ListAdapter(FilterActivity.this,R.layout.adapter_list,level3,stateLevel3);
                    level++;
                    fabNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_send));
                    listView.setAdapter(adapter);
                }
                else if(level == 3) {
                    Intent resultIntent = new Intent(FilterActivity.this,AllFunctionsActivity.class);
                    JSONObject tagsJSON=new JSONObject();
                    try {
                        JSONArray dept = new JSONArray();
                        JSONArray year = new JSONArray();
                        JSONArray degree = new JSONArray();
                        for(int i=0;i<level1.size();i++)
                            if(stateLevel1.get(i))
                                dept.put(level1.get(i));
                        for(int i=0;i<level2.size();i++)
                            if(stateLevel2.get(i))
                                year.put(level2.get(i));
                        for(int i=0;i<level3.size();i++)
                            if(stateLevel3.get(i))
                                degree.put(level3.get(i));
                        tagsJSON.accumulate("dept",dept);
                        tagsJSON.accumulate("year",year);
                        tagsJSON.accumulate("degree",degree);
                    } catch(JSONException e) {
                        Log.e("FilterActivityException", e + "");
                    }
                    resultIntent.putExtra("tagsJSON",tagsJSON.toString());
                    setResult(1,resultIntent);
                    finish();
                }
            }
        });

        fabPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(level==2) {
                    adapter = new ListAdapter(FilterActivity.this,R.layout.adapter_list,level1,stateLevel1);
                    level--;
                    fabPrev.setVisibility(View.INVISIBLE);
                    listView.setAdapter(adapter);
                }
                else if(level==3) {
                    adapter = new ListAdapter(FilterActivity.this,R.layout.adapter_list,level2,stateLevel2);
                    level--;
                    fabNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_arrow_forward));
                    listView.setAdapter(adapter);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (level) {
                    case 1:
                        if (stateLevel1.get(i)) {
                            stateLevel1.set(i, false);
                        } else {
                            stateLevel1.set(i, true);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        if (stateLevel2.get(i)) {
                            stateLevel2.set(i, false);
                        } else {
                            stateLevel2.set(i, true);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        if (stateLevel3.get(i)) {
                            stateLevel3.set(i, false);
                        } else {
                            stateLevel3.set(i, true);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }

}
