package com.delta.campuscomm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter{
    ArrayList<String> tagsList;
    ArrayList<Boolean> stateList;
    public ListAdapter(Context context, int resource
            , ArrayList<String> tagsList, ArrayList<Boolean> stateList) {
        super(context, resource,tagsList);
        this.tagsList = tagsList;
        this.stateList = stateList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.adapter_list, parent, false);
        TextView textView = (TextView)view.findViewById(R.id.textView);
        textView.setText(tagsList.get(position).toUpperCase());
        if(stateList.get(position))
            view.setBackgroundColor(getContext().getResources().getColor(R.color.colorCyan));
        else
            view.setBackgroundColor(getContext().getResources().getColor(R.color.colorPureWhite));
        return view;
    }
}
