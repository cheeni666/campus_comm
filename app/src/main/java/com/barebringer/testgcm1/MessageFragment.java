package com.barebringer.testgcm1;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

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

import static com.barebringer.testgcm1.CommonUtilities.NEW_URL;

public class MessageFragment extends Fragment implements GridViewAdapter.deletebuttonlistener {
    public static String TAG = "TAG";
    GridView gridView;
    String username;
    Integer done;
    ArrayList<String> temparray;
    ArrayList<String> level1 = new ArrayList<String>(Arrays.asList("cse", "ece", "eee", "mech", "chemical", "prod", "ice", "civil", "meta", "archi"));
    ArrayList<String> level2 = new ArrayList<String>();
    ArrayList<String> level3 = new ArrayList<String>(Arrays.asList("btech", "mtech", "other"));
    private static final int MAX_ATTEMPTS = 1;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private OnFragmentInteractionListener mListener;

    ListView listView;
    ArrayAdapter temp_adapter;
    Boolean firsttimeclick;
    Integer level = 0;
    GridViewAdapter gridViewAdapter;
    FloatingActionButton fab;
    Button chk;
    EditText editText;
    Handler toasty = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), "Failed!!!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int cy = c.get(Calendar.YEAR);
        cy = cy % 100;
        level2.add(""+cy);cy--;
        level2.add(""+cy);cy--;
        level2.add(""+cy);cy--;
        level2.add(""+cy);cy--;
        level2.add(""+cy);
        firsttimeclick = true;
        done=0;
        temparray = new ArrayList<String>();
        gridViewAdapter = new GridViewAdapter(getActivity(), temparray);
        final GridViewAdapter.deletebuttonlistener listener = new GridViewAdapter.deletebuttonlistener() {
            @Override
            public void onButtonclicklistener(String value) {
                if (temparray.contains(value))
                    temparray.remove(value);
                gridViewAdapter.notifyDataSetChanged();
                gridView.setAdapter(gridViewAdapter);
            }
        };
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        editText = (EditText) view.findViewById(R.id.editText);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        chk = (Button) view.findViewById(R.id.chk);
        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getVisibility() == View.GONE) listView.setVisibility(View.VISIBLE);
                listView.setBackgroundResource(R.drawable.shadow);
                level++;
                if (level == 4) level = 1;
                temp_adapter = null;
                if (level == 1)
                    temp_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, level1);
                else if (level == 2)
                    temp_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, level2);
                else if (level == 3)
                    temp_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, level3);
                listView.setAdapter(temp_adapter);
                done=1;
            }
        });
        chk.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listView.setVisibility(View.GONE);
                return true;
            }
        });
        chk.setVisibility(View.GONE);
        username = mListener.getusernamemes();
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
                    chk.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.ic_content_send);
                    firsttimeclick = false;
                } else {
                    final String s = editText.getText().toString();
                    {
                        if (done == 0)
                            toasty.sendEmptyMessage(0);
                        if (done == 1) {
                            if (s == null || s.equals("")) return;
                            editText.setText("");
                            new AsyncTask<Void, Void, String>() {
                                @Override
                                protected String doInBackground(Void... params) {
                                    String serverUrl = NEW_URL;
                                    Map<String, String> paramss = new HashMap<String, String>();
                                    paramss.put("action_id", "0");
                                    paramss.put("message", s);
                                    paramss.put("tags", object.toString());
                                    paramss.put("sender", username);
                                    long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                                    for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                                        Log.d(TAG, "Attempt #" + i + " to register");
                                        try {
                                            posta(serverUrl, paramss);
                                            return s;
                                        } catch (IOException e) {
                                            Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                                            Toast.makeText(getActivity(), "Failed to connect!!", Toast.LENGTH_SHORT).show();
                                            if (i == MAX_ATTEMPTS) {
                                                break;
                                            }
                                            try {
                                                Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                                                Thread.sleep(backoff);
                                            } catch (InterruptedException e1) {
                                                // Activity finished before we complete - exit.
                                                Log.d(TAG, "Thread interrupted: abort remaining retries!");
                                                Thread.currentThread().interrupt();
                                                return s;
                                            }
                                            // increase backoff exponentially
                                            backoff *= 2;
                                        }
                                    }
                                    return s;
                                }

                                @Override
                                protected void onPostExecute(String msg) {
                                }
                            }.execute(null, null, null);
                        }
                    }
                }
            }
        });

        temp_adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, level1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            ArrayList<String> temparray1 = new ArrayList<String>();
            ArrayList<String> temparray2 = new ArrayList<String>();
            ArrayList<String> temparray3 = new ArrayList<String>();

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gridView.setVisibility(View.VISIBLE);
                view.setBackgroundColor(Color.CYAN);
                if (level == 1)
                    try {
                        ArrayList<String> list = new ArrayList<String>();
                        JSONArray array = object.getJSONArray("dept");
                        if (array != null)
                            for (int k = 0; k < array.length(); k++)
                                list.add(array.get(k).toString());
                        if (!list.contains(level1.get(i))) {
                            object.accumulate("dept", level1.get(i));
                            temparray1.add(level1.get(i));
                        }
                        for (int z = 0; z < temparray1.size(); z++)
                            if (!temparray.contains(temparray1.get(z)))
                                temparray.add(temparray1.get(z));
                        gridViewAdapter = new GridViewAdapter(getActivity(), temparray);
                        gridViewAdapter.setButtonclicklistener(listener);
                        gridView.setAdapter(gridViewAdapter);
                    } catch (Exception e) {
                        Log.d(TAG, "Exception " + e);
                    }
                if (level == 2)
                    try {
                        ArrayList<String> list = new ArrayList<String>();
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
                        gridViewAdapter = new GridViewAdapter(getActivity(), temparray);
                        gridViewAdapter.setButtonclicklistener(listener);
                        gridView.setAdapter(gridViewAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                if (level == 3)
                    try {
                        ArrayList<String> list = new ArrayList<String>();
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
                        gridViewAdapter = new GridViewAdapter(getActivity(), temparray);
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
            CharSequence charSequence = "status";
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.contains(charSequence))
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                JSONObject js = new JSONObject(line);
                int status = Integer.parseInt(js.getString("status_code"));
                if (status <= 0) toasty.sendEmptyMessage(0);

            } catch (JSONException e) {
                e.printStackTrace();
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
        Log.d(TAG, "Listview Count =" + listView.getCount());
        for (int i = 0; i < listView.getCount(); i++) {
            Log.d(TAG, "Listview Count =" + i);
            if (listView.getItemAtPosition(i).toString().equals(value)) {
                Log.d(TAG, "Changing Background color");
                View view = listView.getAdapter().getView(i, null, listView);
                view.setBackgroundColor(Color.WHITE);
            }
        }

    }

    public interface OnFragmentInteractionListener {
        public String getusernamemes();
    }

}
