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

public class GridViewAdapter extends ArrayAdapter<String>
{
    public static String TAG="TAG";
    public ArrayList<String> stringArrayList;
    DeleteButtonListener listener;

    public interface DeleteButtonListener
    {
        public void onButtonclicklistener(String value);
    }
    public void setButtonclicklistener(DeleteButtonListener listener)
    {
        this.listener=listener;
    }
    public GridViewAdapter(Context context, ArrayList<String> tags)
    {
        super(context, R.layout.gridadapter,tags);
        this.stringArrayList =tags;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.gridadapter,parent,false);
        TextView textview = (TextView)view.findViewById(R.id.tag);
        textview.setText(stringArrayList.get(position));
        Button buttonDelete = (Button)view.findViewById(R.id.delete_button);
        buttonDelete.setTag(position);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                listener.onButtonclicklistener(stringArrayList.get(pos));
            }
        });
        Log.d(TAG,"Item created "+position);
        return view;
    }
}
