package com.barebringer.testgcm1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static com.barebringer.testgcm1.CommonUtilities.SERVER_URL;
import static com.barebringer.testgcm1.CommonUtilities.TAG;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "835229264934";
    TextView reg_id;
    EditText gnome;
    static String name;
    Intent i;
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gnome = (EditText) findViewById(R.id.nametext);
        i = new Intent(this, First.class);
        //connection close
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = store.edit();
        editor.putString("temp", null);
        String tag="STUDENT-/BTECH-/2-/CSE-/";
        editor.putString("defTags",tag);
        editor.apply();
        regid = store.getString("device_regid", null);
        if (regid != null) {
            finish();
            startActivity(i);
        }
        reg_id = (TextView) findViewById(R.id.textView);
        reg_id.setText(regid);
    }

    public void register(View view) {
        name = gnome.getText().toString();
        if (name == null || name.equals("")) return;
        gnome.setText("");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = regid;
                    Log.i("GCM", msg);
                    finish();
                    startActivity(i);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                reg_id.setText(msg);
                SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = store.edit();
                editor.putString("device_regid", regid);
                editor.apply();
                pregister(MainActivity.this, regid);
            }
        }.execute(null, null, null);

    }

    public void unregister(View view) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    gcm.unregister();
                    msg = "Device unregistered";
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

                reg_id.setText(msg);
                SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = store.edit();
                editor.putString("device_regid", null);
                editor.apply();
                punregister(MainActivity.this, regid);
            }
        }.execute(null, null, null);
    }


    static void pregister(final Context context, final String regId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                Log.i(TAG, "registering device (regId = " + regId + ")");
                String serverUrl = SERVER_URL;
                Map<String, String> paramss = new HashMap<String, String>();
                paramss.put("regId", regId);
                paramss.put("name", name);
                long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                    Log.d(TAG, "Attempt #" + i + " to register");
                    try {
                        post(serverUrl, paramss);
                        return msg;
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
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

    static void punregister(final Context context, final String regId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";


                Log.i(TAG, "unregistering device (regId = " + regId + ")");
                String serverUrl = SERVER_URL + "/unregister";
                Map<String, String> paramss = new HashMap<String, String>();
                paramss.put("regId", regId);
                try {
                    post(serverUrl, paramss);
                } catch (IOException e) {

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);

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
