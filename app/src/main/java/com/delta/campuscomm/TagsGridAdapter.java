package com.delta.campuscomm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by RB on 3/5/2016.
 */
public class TagsGridAdapter extends ArrayAdapter<String>{
    ArrayList<String> tags;

    public TagsGridAdapter(Context context,ArrayList<String> tags) {
        super(context, R.layout.gridadapter_tags,tags);
        this.tags = tags;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.gridadapter_tags,parent,false);
        TextView textview = (TextView)view.findViewById(R.id.textView_tag);
        textview.setText(tags.get(position));
        return view;
    }
}
