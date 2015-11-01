package com.barebringer.testgcm1;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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

        //Generate the post for the list view
        TextView username_text = (TextView) customview.findViewById(R.id.custom_username_text);
        TextView mes_text = (TextView) customview.findViewById(R.id.custom_mes_text);
        TextView time_text = (TextView) customview.findViewById(R.id.custom_time_text);
        ImageView colorstrip_image = (ImageView) customview.findViewById(R.id.custom_colorstrip_image);

        int color = Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256));
        colorstrip_image.setBackgroundColor(color);
        try {
            JSONObject j = new JSONObject(complete);
            username_text.setText(j.getString("sender") + " posted");
            mes_text.setText(j.getString("message"));

            //Processing timestamp
            String[] timestamp = j.getString("timestamp").split(" ");
            String[] date = timestamp[0].split("-");
            String[] time = timestamp[1].split(":");
            if (c.get(Calendar.YEAR) != Integer.parseInt(date[0])) {
                int t = c.get(Calendar.YEAR) - Integer.parseInt(date[0]);
                time_text.setText(t + " years ago");
            } else if (1 + c.get(Calendar.MONTH) != Integer.parseInt(date[1])) {
                int t = 1 + c.get(Calendar.MONTH) - Integer.parseInt(date[1]);
                time_text.setText(t + " months ago");
            } else if (c.get(Calendar.DAY_OF_MONTH) != Integer.parseInt(date[2])) {
                int t = c.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(date[2]);
                if (t == 1) time_text.setText("yesterday");
                else time_text.setText(t + " days ago");
            } else {
                time_text.setText("today at " + time[0] + ":" + time[1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customview;
    }

}