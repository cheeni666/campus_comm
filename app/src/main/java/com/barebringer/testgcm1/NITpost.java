package com.barebringer.testgcm1;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.Random;

import static com.barebringer.testgcm1.CommonUtilities.SERVER_URL;
import static com.barebringer.testgcm1.CommonUtilities.NEW_URL;
import static com.barebringer.testgcm1.CommonUtilities.TAG;

public class NITpost extends Fragment {

    private static final int MAX_ATTEMPTS = 1;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    private OnFragmentInteractionListener mListener;
    String username;
    View v;
    TextView status;
    Button yes, no;
    ArrayAdapter cheenisAdapter;

    int update;

    ListView cheenisListView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> posts = new ArrayList<String>(), refreshmes = new ArrayList<>();
    JSONObject tempjson;

    int flag = 1, process = 0;
    String temp;

    Handler clearDB = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MyDBHandler db = new MyDBHandler(getActivity(), null, null, 1);
            SQLiteDatabase dj = db.getDB();
            String query = "DELETE FROM " + "posts" + " WHERE 1;";
            dj.execSQL(query);
            dj.close();
            db.close();
        }
    };
    Handler latest = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    update = 1;
                    String msg = "";
                    String serverUrl = NEW_URL;
                    Map<String, String> paramss = new HashMap<String, String>();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("action_id", "2");
                        jsonObject.put("latest_msg_id", lat_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    paramss.put("json", jsonObject.toString());
                    long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                    for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                        try {
                            posta(serverUrl, paramss);
                            return msg;
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                            failtoast.sendEmptyMessage(0);
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
                                return msg;
                            }
                            // increase backoff exponentially
                            backoff *= 2;
                        }
                    }
                    return msg;
                }

                @Override
                protected void onPostExecute(String msg) {
                }
            }.execute(null, null, null);
        }
    };
    Handler toast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
        }
    };
    Handler failtoast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), "Failed connection", Toast.LENGTH_SHORT).show();
        }
    };
    Handler jsonhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (update == 1) {
                cheenisAdapter = new CustomAdapter(getActivity(), refreshmes);
                cheenisListView.setAdapter(cheenisAdapter);
            }
            if (update == -1) cheenisAdapter.insert(tempjson.toString(), cheenisAdapter.getCount());

        }
    };
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
    int old_id = 0;
    String lat_id = new String();
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
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        username = mListener.getusername1();
        status.setText(username);

        cheenisListView = (ListView) v.findViewById(R.id.listView);
        cheenisAdapter = new CustomAdapter(getActivity(), posts);
        cheenisListView.setAdapter(cheenisAdapter);
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
        cheenisListView.addFooterView(footerView);

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

        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        if (process == 1) return "";
                        toast.sendEmptyMessage(0);
                        process = 1;
                        update = -1;
                        String msg = "";
                        String serverUrl = NEW_URL;
                        if (old_id == 0) {
                            MyDBHandler d = new MyDBHandler(getActivity(), null, null, 1);
                            SQLiteDatabase db = d.getDB();
                            String query = "SELECT * FROM " + "posts" + " WHERE 1 ORDER BY " + "_id" + " ASC;";
                            Cursor c = db.rawQuery(query, null);
                            //Move to the first row in your results
                            c.moveToFirst();
                            db.close();
                            old_id = 0;
                            if (c.getCount() != 0) {
                                old_id = c.getInt(c.getColumnIndex("_id"));
                            } else {
                                failtoast.sendEmptyMessage(0);
                                return "";
                            }
                        }
                        Map<String, String> paramss = new HashMap<String, String>();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("action_id", "3");
                            jsonObject.put("oldest_msg_id", old_id + "");
                            jsonObject.put("no_of_msgs", "20");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        paramss.put("json", jsonObject.toString());
                        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                            Log.d(TAG, "Attempt #" + i + " to register");
                            try {
                                posta(serverUrl, paramss);
                                return msg;
                            } catch (IOException e) {
                                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                                failtoast.sendEmptyMessage(0);
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
                                    return msg;
                                }
                                // increase backoff exponentially
                                backoff *= 2;
                            }
                        }
                        return msg;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        process = 0;
                    }
                }.execute(null, null, null);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        update = 2;
                        String msg = "";
                        String serverUrl = NEW_URL;
                        Map<String, String> paramss = new HashMap<String, String>();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("action_id", "4");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        paramss.put("json", jsonObject.toString());
                        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                            Log.d(TAG, "Attempt #" + i + " to register");
                            try {
                                posta(serverUrl, paramss);
                                return msg;
                            } catch (IOException e) {
                                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                                failtoast.sendEmptyMessage(0);
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
                                    return msg;
                                }
                                // increase backoff exponentially
                                backoff *= 2;
                            }
                        }
                        return msg;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }.execute(null, null, null);

            }
        });
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

    public void clear() {
        MyDBHandler dbHandler = new MyDBHandler(getActivity(), null, null, 1);
        for (int i = 0; i < refreshmes.size(); i++) {
            dbHandler.addName(refreshmes.get(i));
        }
        refreshmes = new ArrayList<>();
    }

    private void posta(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        boolean wak=false;
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
            CharSequence charSequence = "msg";

            String line;
            try {
                wak=true;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(charSequence))
                        break;
                }
                JSONObject js = new JSONObject(line);
                if (update != 2) {
                    if (update == 1) clearDB.sendEmptyMessage(0);
                    int l = Integer.parseInt(js.getString("no_of_msgs"));
                    JSONArray jsonArray = new JSONArray(js.get("messages").toString());
                    refreshmes = new ArrayList<>();
                    int i = 0;
                    for (; i < l; i++) {
                        tempjson = jsonArray.getJSONObject(i);
                        refreshmes.add(tempjson.toString());
                        if (update == -1) jsonhandler.sendEmptyMessage(0);
                    }
                    if (update == 1) jsonhandler.sendEmptyMessage(0);
                } else lat_id = js.getString("latest_id");
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
        if(wak){
            if (update == -1) {
                try {
                    old_id = Integer.parseInt(tempjson.getString("msg_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (update == 1) clear();
            if (update == 2) {
                int templar = Integer.parseInt(lat_id);
                templar = templar - 20;
                lat_id = templar + "";
                latest.sendEmptyMessage(0);
            }
        }
        else failtoast.sendEmptyMessage(0);
    }

}
