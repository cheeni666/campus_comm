package com.delta.campuscomm;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.delta.campuscomm.CommonUtilities.*;
import static com.delta.campuscomm.CommonUtilities.NEW_URL;
import static com.delta.campuscomm.CommonUtilities.TAG;
import static com.delta.campuscomm.CommonUtilities.isFetchNew;
import static com.delta.campuscomm.CommonUtilities.isFetchOld;
import static com.delta.campuscomm.MyDBHandler.TABLE;
import static com.delta.campuscomm.MyDBHandler.COLUMN_ID;
import static com.delta.campuscomm.MyDBHandler.COLUMN_POST;

public class ViewAllPostsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    String username;
    View viewFragment;
    ArrayAdapter listAdapter;

    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> posts = new ArrayList<String>(), refreshmes = new ArrayList<>();
    JSONObject tags = null;
    Integer statusCode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (container == null) {
            return null;
        }
        //getting the view
        viewFragment = inflater.inflate(R.layout.fragment_view_all_posts, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) viewFragment.findViewById(R.id.widgetSwipeViewAllPosts);
        username = mListener.getUserNameViewAllPostsFragment();
        //TODO CHAnge this
        ArrayList<String> tempPosts = new ArrayList<>();
        try {
            JSONObject messagesJSON = new JSONObject(CommonUtilities.messagesJSON);
            JSONArray messages = messagesJSON.getJSONObject("data").getJSONArray("messages");
            for(int i=0;i<messages.length();i++)
                tempPosts.add(messages.get(i).toString());
        }catch (JSONException e) {
            Log.d("JSONEXception",e+"");
        }
        listView = (ListView) viewFragment.findViewById(R.id.listViewPostListViewAllPosts);
        listAdapter = new MessageAdapter(getActivity(), tempPosts);
        listView.setAdapter(listAdapter);
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_message_footer, null, false);
        listView.addFooterView(footerView);

        //the below function is to display all the local messages via null tags
        displayPosts(tags);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isFetchNew){
                    Toast.makeText(getActivity(), "Already Updating", Toast.LENGTH_SHORT).show();
                    return;
                }
                isFetchNew = true;
                swipeRefreshLayout.setRefreshing(true);
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        Integer newId = 0;
                        statusCode = 0;
                        String serverUrl = NEW_URL;

                        MyDBHandler myDBHandler = new MyDBHandler(getActivity(), null, null, 1);
                        SQLiteDatabase db = myDBHandler.getDB();
                        String query = "SELECT * FROM " + TABLE + " WHERE 1 ORDER BY " + COLUMN_ID + " DESC;";
                        Cursor cursor = db.rawQuery(query, null);
                        //Move to the first row in your results
                        cursor.moveToFirst();
                        db.close();
                        //newId contains the latest message id loaded
                        if (cursor.getCount() != 0) {
                            newId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                        }

                        Map<String, String> paramss = new HashMap<String, String>();
                        paramss.put("latest_msg_id", newId + "");
                        try {
                            post(serverUrl, paramss);
                        } catch (IOException e) {
                            Log.d(TAG, "" + e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        swipeRefreshLayout.setRefreshing(false);
                        isFetchNew = false;
                        if(statusCode != 200)
                            Toast.makeText(getActivity(), "Failed to fetch new Msgs", Toast.LENGTH_SHORT).show();
                        else displayPosts(tags);
                    }
                }.execute(null, null, null);

            }
        });

        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFetchOld){
                    Toast.makeText(getActivity(), "Already Updating", Toast.LENGTH_SHORT).show();
                    return;
                }
                isFetchOld = true;
                Toast.makeText(getActivity(), "Wait for a teensy bit", Toast.LENGTH_SHORT).show();
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String serverUrl = NEW_URL;
                        statusCode = 0;
                        int oldId = 0;
                        //old_id gets the oldest message id loaded
                            MyDBHandler d = new MyDBHandler(getActivity(), null, null, 1);
                            SQLiteDatabase db = d.getDB();
                            String query = "SELECT * FROM " + TABLE + " WHERE 1 ORDER BY " + COLUMN_ID + " ASC;";
                            Cursor c = db.rawQuery(query, null);
                            //Move to the first row in your results
                            c.moveToFirst();
                            db.close();
                            if (c.getCount() != 0) {
                                oldId = c.getInt(c.getColumnIndex(COLUMN_ID));
                            }
                        if(oldId == 0)return null;

                        Map<String, String> paramss = new HashMap<String, String>();
                        paramss.put("oldest_msg_id", oldId + "");
                        paramss.put("no_of_msgs", "50");

                        try {
                            post(serverUrl, paramss);
                        } catch (IOException e) {
                            Log.d(TAG, "" + e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        isFetchOld = false;
                        if(statusCode != 200)
                            Toast.makeText(getActivity(), "Failed to fetch old Msgs", Toast.LENGTH_SHORT).show();
                        else displayPosts(tags);
                    }
                }.execute(null, null, null);
            }
        });

        return viewFragment;
    }

    public void displayPosts(final JSONObject tags){
        posts = new ArrayList<String>();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                //query to get all messages sorted by latest recieved and then sent to upadte_list to
                //add in the list
                MyDBHandler myDBHandler = new MyDBHandler(getActivity(), null, null, 1);
                SQLiteDatabase db = myDBHandler.getDB();
                String query = "SELECT * FROM " + TABLE + " WHERE 1 ORDER BY " + COLUMN_ID + " DESC;";
                Cursor cursor = db.rawQuery(query, null);
                //Move to the first row in your results
                cursor.moveToFirst();
                db.close();
                while (!cursor.isAfterLast()) {
                    if (cursor.getString(cursor.getColumnIndex(COLUMN_POST)) != null) {
                        String tableData = cursor.getString(cursor.getColumnIndex(COLUMN_POST));
                        if(isTagsPresent(tableData, tags))
                            posts.add(tableData);
                    }
                    cursor.moveToNext();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String msg) {
                listAdapter.notifyDataSetChanged();
            }
        }.execute(null, null, null);
    }

    public boolean isTagsPresent(String data, JSONObject tags){
        if(tags == null)return true;
        else{
            int index;
            JSONArray names = tags.names();
            try {
                JSONObject jsonData = new JSONObject(data);
                JSONObject dataTags = jsonData.getJSONObject("tags");
                for(index = 0 ; index < names.length() ; index++){
                    String name = names.getString(index);
                    JSONArray dataTagArray = dataTags.getJSONArray(name);
                    JSONArray tagArray = tags.getJSONArray(name);
                    for(int iteri = 0 ; iteri < tagArray.length() ; iteri++){
                        boolean isFound = false;
                        for(int iterj = 0 ; iterj < dataTagArray.length() ; iterj++){
                            if(tagArray.getString(iteri).contentEquals(dataTagArray.getString(iterj)))
                                isFound = true;
                        }
                        if(!isFound)return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
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
        public String getUserNameViewAllPostsFragment();
    }

    public void addToDB() {
        MyDBHandler dbHandler = new MyDBHandler(getActivity(), null, null, 1);
        //adding all refresmes into the db
        for (int i = 0; i < refreshmes.size(); i++) {
            dbHandler.add(refreshmes.get(i));
        }
    }

    private void post(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String tmpline = "", response = "";
            try {
                while ((tmpline = reader.readLine()) != null) {
                    response += tmpline;
                }
                JSONObject jsonResponse = new JSONObject(response);

                statusCode = jsonResponse.getInt("status");
                if(statusCode == 200){
                    int l = jsonResponse.getJSONObject("data").getInt("no_of_messages");

                    JSONArray jsonArray = jsonResponse.getJSONObject("data").getJSONArray("messages");
                    //refreshmes is a temporary array to store all the messages which will
                    //be added to the db later
                    refreshmes = new ArrayList<>();
                    int i;
                    for (i = 0; i < l; i++) {
                        refreshmes.add(jsonArray.getJSONObject(i).toString());
                    }
                    addToDB();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

}
