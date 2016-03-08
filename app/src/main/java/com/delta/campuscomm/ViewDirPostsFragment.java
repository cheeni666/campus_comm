package com.delta.campuscomm;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
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
import static com.delta.campuscomm.MyDBHandler.COLUMN_ID;
import static com.delta.campuscomm.MyDBHandler.COLUMN_POST;

public class ViewDirPostsFragment extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (container == null) {
            return null;
        }
        //getting the view
        viewFragment = inflater.inflate(R.layout.fragment_view_dir_posts, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) viewFragment.findViewById(R.id.widgetSwipeViewDirPosts);
        username = mListener.getUserNameViewDirPostsFragment();

        listView = (ListView) viewFragment.findViewById(R.id.listViewPostListViewDirPosts);
        listAdapter = new MessageAdapter(getActivity(), posts);
        listView.setAdapter(listAdapter);
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_message_footer, null, false);
        listView.addFooterView(footerView);

        tags = mListener.getTagsDirPostsFragment();

        displayPosts(tags);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isFetchNew){
                    Toast.makeText(getActivity(), "Already Updating", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                isFetchNew = true;
                swipeRefreshLayout.setRefreshing(true);
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        Integer newId = 1;
                        statusCode = -1;
                        String serverUrl = NEW_URL;

                        Cursor cursor = myDBHandler.getEntries("DESC");
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
                        if (statusCode != 200){
                            if(statusCode == -1)
                                Toast.makeText(getActivity(), "Failed to fetch new Msgs", Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getActivity(), "No new Msgs", Toast.LENGTH_SHORT).show();
                        }
                        displayPosts(tags);
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
                        String serverUrl = OLD_URL;
                        statusCode = -1;
                        Integer oldId = 1;
                        //old_id gets the oldest message id loaded
                        Cursor cursor = myDBHandler.getEntries("ASC");
                        if (cursor.getCount() != 0) {
                            oldId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                        }
                        if (oldId == 1) return null;

                        Map<String, String> paramss = new HashMap<String, String>();
                        paramss.put("oldest_msg_id", oldId + "");
                        paramss.put("no_of_messages", "50");

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
                        if (statusCode != 200){
                            if(statusCode == -1)
                                Toast.makeText(getActivity(), "Failed to fetch old Msgs", Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getActivity(), "No old Msgs", Toast.LENGTH_SHORT).show();
                        }
                        displayPosts(tags);
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
                Cursor cursor = myDBHandler.getEntries("DESC");
                while (!cursor.isAfterLast()) {
                    if (cursor.getString(cursor.getColumnIndex(COLUMN_POST)) != null) {
                        String tableData = cursor.getString(cursor.getColumnIndex(COLUMN_POST));
                        if(isTagsPresent(tableData, tags)){
                            try {
                                JSONObject parsedTableData = new JSONObject(tableData);
                                if(parsedTableData.getString("Sender").contentEquals("director"))
                                    posts.add(tableData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    cursor.moveToNext();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String msg) {
                listAdapter = new MessageAdapter(getActivity(), posts);
                listView.setAdapter(listAdapter);
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
                JSONObject dataTags = new JSONObject(jsonData.getString("tags"));
                for(index = 0 ; index < names.length() ; index++){
                    String name = names.getString(index);
                    JSONArray dataTagArray = dataTags.getJSONArray(name);
                    JSONArray tagArray = tags.getJSONArray(name);
                    boolean isFound = false;
                    for(int iteri = 0 ; iteri < tagArray.length() ; iteri++){
                        for(int iterj = 0 ; iterj < dataTagArray.length() ; iterj++){
                            if(tagArray.getString(iteri).contentEquals(dataTagArray.getString(iterj)))
                                isFound = true;
                        }
                    }
                    if(dataTagArray.length() * tagArray.length() == 0)isFound = true;
                    if(!isFound)return false;
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
        public String getUserNameViewDirPostsFragment();
        public JSONObject getTagsDirPostsFragment();
    }

    public void addToDB() {
        //adding all refresmes into the db
        Log.d(TAG, "adding to db");
        for (int i = 0; i < refreshmes.size(); i++) {
            myDBHandler.add(refreshmes.get(i));
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
                Log.d(TAG, response);
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
