package com.barebringer.testgcm1;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class NITpost extends Fragment {

    private OnFragmentInteractionListener mListener;
    String username;
    View v;
    TextView status;
    Button yes, no;
    ArrayAdapter cheenisAdapter;

    ListView cheenisListView;
    ArrayList<String> posts = new ArrayList<String>();

    int flag = 1;
    String temp;
    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            cheenisAdapter.insert(temp, 0);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            cheenisAdapter.notifyDataSetChanged();
        }
    };
    Runnable r = new Runnable() {
        @Override
        public void run() {
            while (flag == 1) {
                flag = mListener.scraper1();
                String t = mListener.newmes();
                if (t == null) continue;
                else if (temp == null) {
                    temp = t;
                    h.sendEmptyMessage(0);
                } else if (!temp.equals(t)) {
                    temp = t;
                    h.sendEmptyMessage(0);
                }
            }
        }
    };
    Thread t = new Thread(r);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (container == null) {
            return null;
        }
        v = inflater.inflate(R.layout.fragment_nitpost, container, false);
        status = (TextView) v.findViewById(R.id.header1);
        yes = (Button) v.findViewById(R.id.yes1);
        no = (Button) v.findViewById(R.id.no1);
        username = mListener.getusername1();
        status.setText(username);

        cheenisListView = (ListView) v.findViewById(R.id.listView);
        cheenisAdapter = new CustomAdapter(getActivity(), posts);
        cheenisListView.setAdapter(cheenisAdapter);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                MyDBHandler d = new MyDBHandler(getActivity(), null, null, 1);
                SQLiteDatabase db = d.getDB();
                String query = "SELECT * FROM " + "posts" + " WHERE 1 ORDER BY " + "_id" + " DESC;";
                Cursor c = db.rawQuery(query, null);
                //Move to the first row in your results
                c.moveToFirst();
                db.close();
                while (!c.isAfterLast()) {
                    if (c.getString(c.getColumnIndex("post")) != null) {
                        posts.add(c.getString(c.getColumnIndex("post")));
                        handler.sendEmptyMessage(0);
                    }
                    c.moveToNext();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
        t.start();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout1();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yes.setAlpha(0);
                no.setAlpha(0);
                cheenisListView.setAlpha(1);
                yes.setEnabled(false);
                no.setEnabled(false);
            }
        });
        status.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                yes.setAlpha(1);
                no.setAlpha(1);
                cheenisListView.setAlpha(0);
                yes.setEnabled(true);
                no.setEnabled(true);
                return true;
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        public String getusername1();

        public void logout1();

        public int scraper1();

        public String newmes();
    }

}
