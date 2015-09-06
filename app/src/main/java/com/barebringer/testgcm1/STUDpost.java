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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.konifar.fab_transformation.FabTransformation;
import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.barebringer.testgcm1.CommonUtilities.POST_URL;
import static com.barebringer.testgcm1.CommonUtilities.TAG;
import static com.barebringer.testgcm1.CommonUtilities.level1;
import static com.barebringer.testgcm1.CommonUtilities.level2_1;
import static com.barebringer.testgcm1.CommonUtilities.level2_2;
import static com.barebringer.testgcm1.CommonUtilities.level3;

public class STUDpost extends Fragment {

    private OnFragmentInteractionListener mListener;
    String username,dispstring=new String();
    View v;
    Button send;
    EditText mes;
    ActionButton actionButton;
    ArrayList<String> tags = new ArrayList<String>();
    ArrayAdapter tagAdapter;
    ListView l;
    JSONObject polosjson=new JSONObject();
    String polos=new String();
    Button b;
    int level = 1;
    boolean btech, mtech;
    int done = 0, temp = 0;
    TextView goh;
    private static final int MAX_ATTEMPTS = 1;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    Handler failtoast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), "Failed connection", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (container == null) {
            return null;
        }
        v = inflater.inflate(R.layout.fragment_studpost, container, false);

        l = (ListView) v.findViewById(R.id.listView2);
        b = (Button) v.findViewById(R.id.buttontag);
        b.setEnabled(false);
        goh = (TextView) v.findViewById(R.id.textView5);

        tags = level1;
        tagAdapter = new Adapter(getActivity(), tags);
        l.setAdapter(tagAdapter);

        actionButton = (ActionButton) v.findViewById(R.id.action_button);
        actionButton.setRippleEffectEnabled(true);
        actionButton.setImageResource(R.drawable.tags);
        actionButton.setButtonColor(Color.parseColor("#6d84b4"));
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (done == 1)
                    Toast.makeText(getActivity(), "Clear Tags First (Long click)", Toast.LENGTH_SHORT).show();
                if (position != tags.size() - 1 && done == 0) {
                    ImageView kilua = (ImageView) view.findViewById(R.id.imageViews);
                    if (kilua.getBaseline() != 2) {
                        kilua.setBaseline(2);
                        kilua.setBackgroundColor(Color.parseColor("#000000"));
                        polos+=parent.getItemAtPosition(position)+",";
                        dispstring+=parent.getItemAtPosition(position).toString() + ", ";
                        goh.setText(dispstring);
                        if (parent.getItemAtPosition(position).toString().equals("btech"))
                            btech = true;
                        if (parent.getItemAtPosition(position).toString().equals("mtech"))
                            mtech = true;
                        temp++;
                        if(btech&&mtech){
                            done = 1;
                            FabTransformation.with(actionButton)
                                    .transformFrom(l);
                            b.setAlpha(0);
                            b.setEnabled(false);
                            try {
                                polosjson.put("degree", polos);
                                polosjson.put("year","all,");
                                polosjson.put("dept","all,");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (position == tags.size() - 1 && done == 0 && temp != 0) {
                    if (level == 1) {
                        if (btech || mtech) {
                            try {
                                polosjson.put("degree", polos);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (btech) tags = level2_1;
                            if (mtech) tags = level2_2;
                        }
                    }
                    if (level == 2) {

                        try {
                            polosjson.put("year", polos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tags = level3;

                    }
                    if (level == 3) {

                        try {
                            polosjson.put("dept", polos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    polos = new String();
                    temp = 0;
                    level++;
                    if (level >= 4) {
                        done = 1;
                        FabTransformation.with(actionButton)
                                .transformFrom(l);
                        b.setAlpha(0);
                        b.setEnabled(false);
                    }
                    tagAdapter = new Adapter(getActivity(), tags);
                    l.setAdapter(tagAdapter);
                    dispstring+="/";
                    goh.setText(dispstring);
                }
            }
        });
        actionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tags = level1;
                polos = new String();
                polosjson=new JSONObject();
                btech = false;
                mtech = false;
                dispstring=new String();
                level = 1;
                done = 0;
                tagAdapter = new Adapter(getActivity(), tags);
                l.setAdapter(tagAdapter);
                goh.setText(dispstring);
                return true;
            }
        });
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FabTransformation.with(actionButton)
                        .transformTo(l);
                b.setAlpha(1);
                b.setEnabled(true);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FabTransformation.with(actionButton)
                        .transformFrom(l);
                b.setAlpha(0);
                b.setEnabled(false);
            }
        });

        send = (Button) v.findViewById(R.id.send2);
        mes = (EditText) v.findViewById(R.id.editText2);
        username = mListener.getusername2();
        if (!username.equals("director")) {
            actionButton.setEnabled(false);
            done=1;
            try {
                polosjson.put("degree", "all,");
                polos=new String();
                polos+="all,";
                polosjson.put("year", polos);
                polosjson.put("dept", polos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (done == 0)
                    Toast.makeText(getActivity(), "Tags Incomplete", Toast.LENGTH_SHORT).show();
                if (done == 1) {
                    final String msg = mes.getText().toString();
                    if (msg == null || msg.equals("")) return;
                    mes.setText("");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message", msg);
                        jsonObject.put("tags", polosjson.toString());
                        jsonObject.put("username", username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String k = jsonObject.toString();
                     new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            String serverUrl = POST_URL;
                            Map<String, String> paramss = new HashMap<String, String>();
                            paramss.put("message", k);
                            long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                            for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                                Log.d(TAG, "Attempt #" + i + " to register");
                                try {
                                    post(serverUrl, paramss);
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
        public String getusername2();

        public void logout2();
    }

    private static void post(String endpoint, Map<String, String> params)
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
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
