package com.delta.campuscomm;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.delta.campuscomm.CommonUtilities.*;

public class SendPostsFragment extends Fragment implements GridViewAdapter.DeleteButtonListener {
    public static String TAG = "TAG";
    GridView gridView;
    String username;
    Integer done;
    ArrayList<String> temparray;
    ArrayList<String> level1 = new ArrayList<String>(Arrays.asList("cse", "ece", "eee", "mech", "chemical", "prod", "ice", "civil", "meta", "archi"));
    ArrayList<String> level2 = new ArrayList<String>();
    ArrayList<String> level3 = new ArrayList<String>(Arrays.asList("btech", "mtech", "other"));
    ArrayList<String> list;
    ArrayList<Boolean> stateLevel1 = new ArrayList<>();
    ArrayList<Boolean> stateLevel2 = new ArrayList<>();
    ArrayList<Boolean> stateLevel3 = new ArrayList<>();
    private OnFragmentInteractionListener mListener;

    ListView listView;
    ListAdapter listAdapter;
    Boolean firsttimeclick;
    Integer level = 0;
    GridViewAdapter gridViewAdapter;
    FloatingActionButton fabTags;
    FloatingActionButton fab;
    EditText editText;
    Integer statusCode;

    public class MJSONArray extends JSONArray {

        @Override
        public Object remove(int index) {

            JSONArray output = new JSONArray();
            int len = this.length();
            for (int i = 0; i < len; i++)   {
                if (i != index) {
                    try {
                        output.put(this.get(i));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return output;
            //return this; If you need the input array in case of a failed attempt to remove an item.
        }
    }

    Calendar calendar;
    int cy;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void initialize() {
        calendar = Calendar.getInstance();
        cy = calendar.get(Calendar.YEAR);
        cy = cy % 100;
        level2 = new ArrayList<String>();
        level2.add("" + cy);
        cy--;
        level2.add("" + cy);
        cy--;
        level2.add("" + cy);
        cy--;
        level2.add("" + cy);
        cy--;
        level2.add("" + cy);
        firsttimeclick = true;
        done = 0;
        temparray = new ArrayList<String>();
        for(int i=0;i<10;i++)
            stateLevel1.add(false);
        for(int i=0;i<5;i++)
            stateLevel2.add(false);
        for(int i=0;i<3;i++)
            stateLevel3.add(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initialize();
        gridViewAdapter = new GridViewAdapter(getActivity(), temparray);
        final GridViewAdapter.DeleteButtonListener listener = new GridViewAdapter.DeleteButtonListener() {
            @Override
            public void onButtonclicklistener(String value) {
                if (temparray.contains(value)) {
                    Log.d("LOGGING",value);
                    temparray.remove(value);
                }
                for(int i=0;i<level1.size();i++) {
                    if(level1.get(i).equals(value))
                        stateLevel1.set(i,false);
                }
                for(int i=0;i<level2.size();i++) {
                    if(level2.get(i).equals(value))
                        stateLevel2.set(i,false);
                }
                for(int i=0;i<level3.size();i++) {
                    if(level3.get(i).equals(value))
                        stateLevel3.set(i,false);
                }
                listAdapter.notifyDataSetChanged();
                gridViewAdapter.notifyDataSetChanged();
                //gridView.setAdapter(gridViewAdapter);
            }
        };
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_posts, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        editText = (EditText) view.findViewById(R.id.editText);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fabTags = (FloatingActionButton) view.findViewById(R.id.button_tags);
        fabTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getVisibility() == View.GONE)
                    listView.setVisibility(View.VISIBLE);
                level++;
                if (level == 4)
                    level = 1;
                listAdapter = null;
                if (level == 1) {
                    listAdapter = new ListAdapter(getActivity(), R.layout.adapter_list, level1,stateLevel1);
                }
                else if (level == 2) {
                    listAdapter = new ListAdapter(getActivity(), R.layout.adapter_list, level2, stateLevel2);
                }
                else if (level == 3) {
                    listAdapter = new ListAdapter(getActivity(), R.layout.adapter_list, level3, stateLevel3);
                }
                listView.setAdapter(listAdapter);
                done = 1;
            }
        });
        fabTags.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listView.setVisibility(View.GONE);
                return true;
            }
        });
        fabTags.setVisibility(View.GONE);
        username = mListener.getUserNameSendPostsFragment();
        listView = (ListView) view.findViewById(R.id.tags_list);

        final JSONObject object = new JSONObject();
        try {
            object.put("dept", new JSONArray());
            object.put("year", new JSONArray());
            object.put("degree", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firsttimeclick) {
                    editText.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.VISIBLE);
                    fabTags.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.ic_content_send);
                    firsttimeclick = false;
                } else {
                    final String message = editText.getText().toString();
                    {
                        if (done == 0)
                            Toast.makeText(getActivity(), "Cannot Send!!", Toast.LENGTH_SHORT).show();
                        if (done == 1) {
                            if (message == null || message.equals("")) return;
                            editText.setText("");
                            new AsyncTask<Void, Void, String>() {
                                @Override
                                protected String doInBackground(Void... params) {
                                    statusCode = 0;
                                    String serverUrl = SEND_URL;
                                    Map<String, String> paramss = new HashMap<String, String>();
                                    paramss.put("message", message);
                                    paramss.put("tags", object.toString());
                                    paramss.put("sender", username);
                                        try {
                                            posta(serverUrl, paramss);
                                        } catch (IOException e) {
                                            Toast.makeText(getActivity(), "Failed to connect!!", Toast.LENGTH_SHORT).show();
                                        }
                                    return message;
                                }

                                @Override
                                protected void onPostExecute(String msg) {
                                    if(statusCode == 200)
                                        Toast.makeText(getActivity(), "Post Sent", Toast.LENGTH_SHORT).show();
                                    else Toast.makeText(getActivity(), "Send Failed", Toast.LENGTH_SHORT).show();
                                }
                            }.execute(null, null, null);
                        }
                    }
                }
            }
        });

        listAdapter = new ListAdapter(getActivity(), R.layout.adapter_list, level1,stateLevel1);
        gridViewAdapter = new GridViewAdapter(getActivity(), temparray);
        gridView.setAdapter(gridViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            ArrayList<String> temparray1 = new ArrayList<String>();
            ArrayList<String> temparray2 = new ArrayList<String>();
            ArrayList<String> temparray3 = new ArrayList<String>();

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gridView.setVisibility(View.VISIBLE);
                if (level == 1)
                    try {
                        list = new ArrayList<String>();
                        if(stateLevel1.get(i)) {
                            stateLevel1.set(i, false);
                            JSONArray jsonArray = object.getJSONArray("dept");
                            for(int t=0;t<jsonArray.length();t++) {
                                Log.d("JSONARRAY", jsonArray.get(t).toString());
                                if (jsonArray.get(t).toString().equals(level1.get(i)))
                                    object.getJSONArray("dept").remove(t);
                            }
                            temparray.remove(level1.get(i));
                        }
                        else {
                            stateLevel1.set(i, true);
                            object.accumulate("dept", level1.get(i));
                            temparray.add(level1.get(i));
                        }
                        /*JSONArray array = object.getJSONArray("dept");
                        if (array != null)
                            for (int k = 0; k < array.length(); k++)
                                list.add(array.get(k).toString());
                        if (!list.contains(level1.get(i))) {
                            object.accumulate("dept", level1.get(i));
                            temparray1.add(level1.get(i));
                        }
                        for (int z = 0; z < temparray1.size(); z++)
                            if (!temparray.contains(temparray1.get(z)))
                                temparray.add(temparray1.get(z));*/
                        listAdapter.notifyDataSetChanged();
                        gridViewAdapter.notifyDataSetChanged();
                        gridViewAdapter.setButtonclicklistener(listener);
                    } catch (Exception e) {
                        Log.d(TAG, "Exception " + e);
                    }
                if (level == 2)
                    try {
                        list = new ArrayList<String>();
                        if(stateLevel2.get(i))
                            stateLevel2.set(i,false);
                        else
                            stateLevel2.set(i,true);
                        JSONArray array = object.getJSONArray("year");
                        if (array != null)
                            for (int k = 0; k < array.length(); k++)
                                list.add(array.get(k).toString());
                        if (!list.contains(level2.get(i))) {
                            object.accumulate("year", level2.get(i));
                            temparray2.add(level2.get(i));
                        }
                        for (int z = 0; z < temparray2.size(); z++)
                            if (!temparray.contains(temparray2.get(z)))
                                temparray.add(temparray2.get(z));
                        listAdapter.notifyDataSetChanged();
                        gridViewAdapter.notifyDataSetChanged();
                        gridViewAdapter.setButtonclicklistener(listener);
                        gridView.setAdapter(gridViewAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                if (level == 3)
                    try {
                        list = new ArrayList<String>();
                        if(stateLevel3.get(i))
                            stateLevel3.set(i,false);
                        else
                            stateLevel3.set(i,true);
                        JSONArray array = object.getJSONArray("degree");
                        if (array != null)
                            for (int k = 0; k < array.length(); k++)
                                list.add(array.get(k).toString());
                        if (!list.contains(level3.get(i))) {
                            object.accumulate("degree", level3.get(i));
                            temparray3.add(level3.get(i));
                        }
                        for (int z = 0; z < temparray3.size(); z++)
                            if (!temparray.contains(temparray3.get(z)))
                                temparray.add(temparray3.get(z));
                        listAdapter.notifyDataSetChanged();
                        gridViewAdapter.notifyDataSetChanged();
                        gridViewAdapter.setButtonclicklistener(listener);
                        gridView.setAdapter(gridViewAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
        return view;
    }

    private void posta(String endpoint, Map<String, String> params)
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
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String tmpline = "", response = "";
            try {
                while ((tmpline = reader.readLine()) != null) {
                        response += tmpline;
                }
                JSONObject js = new JSONObject(response);
                statusCode = js.getInt("status");

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


    @Override
    public void onButtonclicklistener(String value) {
        if (temparray.contains(value))
            temparray.remove(value);
        for(int i=0;i<level1.size();i++) {
            if(level1.get(i).equals(value))
                stateLevel1.set(i,false);
        }
        for(int i=0;i<level2.size();i++) {
            if(level2.get(i).equals(value))
                stateLevel2.set(i,false);
        }
        for(int i=0;i<level3.size();i++) {
            if(level3.get(i).equals(value))
                stateLevel3.set(i,false);
        }
        gridViewAdapter.notifyDataSetChanged();
        listAdapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        public String getUserNameSendPostsFragment();
    }

}