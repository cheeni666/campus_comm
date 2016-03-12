package com.delta.campuscomm;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class MessageAdapter extends ArrayAdapter<String> {
    Random random = new Random();

    public MessageAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.layout_message_adapter, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customview = layoutInflater.inflate(R.layout.layout_message_adapter, parent, false);

        String item = getItem(position);
        Calendar calendar = Calendar.getInstance();

        //Generate the post for the list view
        TextView textViewUsername = (TextView) customview.findViewById(R.id.textViewUsernameMessageAdapter);
        TextView textViewMessage = (TextView) customview.findViewById(R.id.textViewMessageMessageAdapter);
        TextView textViewTime = (TextView) customview.findViewById(R.id.textViewTimeMessageAdapter);
        ImageView imageViewStrip = (ImageView) customview.findViewById(R.id.imageViewStripMessageAdapter);
        TextView textViewTags = (TextView) customview.findViewById(R.id.textView_tags);
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        imageViewStrip.setBackgroundColor(color);
        try {
            JSONObject jsonItem = new JSONObject(item);
            textViewUsername.setText(jsonItem.getString("Sender") + " posted");
            textViewMessage.setText(jsonItem.getString("Message"));
            String tagsString = jsonItem.getString("tags");
            JSONObject tagsObject = new JSONObject(tagsString);
            JSONArray deptArray = tagsObject.getJSONArray("dept");
            JSONArray yearArray = tagsObject.getJSONArray("year");
            JSONArray degreeArray = tagsObject.getJSONArray("degree");
            ArrayList<String> tags = new ArrayList<>();
            for(int i=0;i<deptArray.length();i++)
                tags.add(deptArray.getString(i));
            for(int i=0;i<yearArray.length();i++)
                tags.add(yearArray.getString(i));
            for(int i=0;i<degreeArray.length();i++)
                tags.add(degreeArray.getString(i));
            //Processing timestamp
            String[] timestamp = jsonItem.getString("created_at").split(" ");
            String[] date = timestamp[0].split("-");
            String[] time = timestamp[1].split(":");
            if (calendar.get(Calendar.YEAR) != Integer.parseInt(date[0])) {
                textViewTime.setText(timestamp[0]);
            } else if (1 + calendar.get(Calendar.MONTH) != Integer.parseInt(date[1])) {
                textViewTime.setText(timestamp[0]);
            } else if (calendar.get(Calendar.DAY_OF_MONTH) != Integer.parseInt(date[2])) {
                int t = calendar.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(date[2]);
                if (t == 1) textViewTime.setText("yesterday");
                else textViewTime.setText(timestamp[0]);
            } else {
                textViewTime.setText("today at " + time[0] + ":" + time[1]);
            }

            for(int i=0;i<tags.size();i++)
                textViewTags.append(tags.get(i).toUpperCase()+", ");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customview;
    }

}