package com.barebringer.testgcm1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class CustomAdapter extends ArrayAdapter<String> {
    private Context cont;
    Random r = new Random();

    public CustomAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.custom, resource);
        cont = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater cheenisInflater = LayoutInflater.from(getContext());
        View customview = cheenisInflater.inflate(R.layout.custom, parent, false);
        String complete = getItem(position);
        Calendar c = Calendar.getInstance();
        TextView identitytext = (TextView) customview.findViewById(R.id.identitytext);
        TextView posttext = (TextView) customview.findViewById(R.id.posttext);
        TextView times = (TextView) customview.findViewById(R.id.timestamp);
        ImageView v = (ImageView) customview.findViewById(R.id.strip);
        int color = Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256));
        v.setBackgroundColor(color);
        try {
            JSONObject j = new JSONObject(complete);
            identitytext.setText(j.getString("username") + " posted");
            posttext.setText(j.getString("message"));
            String[] timestamp = j.getString("timestamp").split(":");
            if (!timestamp[0].equals(c.get(Calendar.YEAR) + "")) {
                int t = Integer.parseInt(c.get(Calendar.YEAR) + "") - Integer.parseInt(timestamp[0]);
                times.setText(t + " years ago");
            } else if (!timestamp[1].equals(c.get(Calendar.MONTH) + "")) {
                int t = Integer.parseInt(c.get(Calendar.MONTH) + "") - Integer.parseInt(timestamp[1]);
                times.setText(t + " months ago");
            } else if (!timestamp[2].equals(c.get(Calendar.DAY_OF_MONTH) + "")) {
                int t = Integer.parseInt(c.get(Calendar.DAY_OF_MONTH) + "") - Integer.parseInt(timestamp[2]);
                times.setText(t + " days ago");
            } else if (!timestamp[3].equals(c.get(Calendar.HOUR_OF_DAY) + "")) {
                int t = Integer.parseInt(c.get(Calendar.HOUR_OF_DAY) + "") - Integer.parseInt(timestamp[3]);
                times.setText(t + " hours ago");
            } else if (!timestamp[4].equals(c.get(Calendar.MINUTE) + "")) {
                int t = Integer.parseInt(c.get(Calendar.MINUTE) + "") - Integer.parseInt(timestamp[4]);
                times.setText(t + " minutes ago");
            } else if (!timestamp[5].equals(c.get(Calendar.SECOND) + "")) {
                int t = Integer.parseInt(c.get(Calendar.SECOND) + "") - Integer.parseInt(timestamp[5]);
                times.setText(t + " seconds ago");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customview;
    }

}