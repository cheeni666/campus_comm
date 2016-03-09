package com.delta.campuscomm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RB on 3/5/2016.
 */
public class TagsGridAdapter extends RecyclerView.Adapter<TagsGridAdapter.ViewHolder> {

    ArrayList<String> tags;
    Context context;

    public TagsGridAdapter(Context context,ArrayList<String> tags) {
        this.tags = tags;
        this.context = context;
        Log.d("tags",this.tags.toString());
    }

    @Override
    public TagsGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridadapter_tags, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.textView_tag);
        }
    }

    @Override
    public void onBindViewHolder(TagsGridAdapter.ViewHolder holder, int position) {
        holder.textView.setText(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }
}
