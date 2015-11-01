package com.barebringer.testgcm1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.barebringer.testgcm1.CommonUtilities.SERVER_URL;
import static com.barebringer.testgcm1.CommonUtilities.PROJECT_NUMBER;
import static com.barebringer.testgcm1.CommonUtilities.TAG;

public class Author extends Activity {
    EditText username_edittext, password_edittext;
    GoogleCloudMessaging gcm;
    String regid = new String();
    static String username, password;
    Intent i;
    Integer status;

    ProgressBar spinner;
    TelephonyManager t;

    Handler toast = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            spinner.setVisibility(View.GONE);
            username_edittext.setEnabled(true);
            password_edittext.setEnabled(true);
            Toast.makeText(Author.this, "Authentication failed", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        i = new Intent(this, Posts.class);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        username_edittext = (EditText) findViewById(R.id.author_username_edittext);
        password_edittext = (EditText) findViewById(R.id.author_password_edittext);

        t = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    public void submit(View v) {
        username = username_edittext.getText().toString();
        password = password_edittext.getText().toString();
        if (username == null || username.equals("")) return;

        spinner.setVisibility(View.VISIBLE);
        username_edittext.setEnabled(false);
        password_edittext.setEnabled(false);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    prepost(Author.this, regid);
                } catch (IOException ex) {
                    toast.sendEmptyMessage(0);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String msg) {
                //Write GCMid to the shared memory for future reference
                SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = store.edit();
                editor.putString("GCMid", regid);
                editor.apply();
            }
        }.execute(null, null, null);

    }

    void prepost(final Context context, final String regId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //if login successfull go to next activity
                if (status > 0) {
                    finish();
                    startActivity(i);
                    return;
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                Log.i(TAG, "registering device (regId = " + regId + ")");
                String serverUrl = SERVER_URL;

                Map<String, String> paramss = new HashMap<String, String>();
                paramss.put("username", username);
                paramss.put("password", password);
                paramss.put("gcmid", regId);
                paramss.put("action_id", "0");
                paramss.put("ad_id", t.getDeviceId());

                try {
                    post(serverUrl, paramss);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to register");
                    toast.sendEmptyMessage(0);
                }
                return null;

            }
        }.execute(null, null, null);
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

            CharSequence charSequence = "status_id";
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    //Check whether response contains status_id
                    Log.d(TAG, line);
                    if (line.contains(charSequence)) {
                        break;
                    }
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
                //Processing response
                JSONObject js = new JSONObject(line);
                status = Integer.parseInt(js.getString("status_id"));
                if (status > 0) {
                    //Write username to shared mem if succesfull for the mainactivity to log
                    //in automatically the second time
                    SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = store.edit();
                    editor.putString("username", username);
                    editor.putString("userid", js.get("user_id").toString());
                    editor.apply();
                } else toast.sendEmptyMessage(0);
            } catch (JSONException e) {
                e.printStackTrace();
                toast.sendEmptyMessage(0);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
