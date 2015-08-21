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
import static com.barebringer.testgcm1.CommonUtilities.TAG;

public class Author extends Activity {
    EditText user, pass;
    GoogleCloudMessaging gcm;
    String regid=new String();
    String PROJECT_NUMBER = "835229264934";
    static String name,password;
    Intent i;
    ProgressBar spinner;
    private static final int MAX_ATTEMPTS = 3;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    TelephonyManager t;

    Handler h=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            toast();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        t= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        i=new Intent(this,Posts.class);
        user = (EditText) findViewById(R.id._user);
        pass = (EditText) findViewById(R.id._password);
    }

    void pregister(final Context context, final String regId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                Log.i(TAG, "registering device (regId = " + regId + ")");
                String serverUrl = SERVER_URL;
                Map<String, String> paramss = new HashMap<String, String>();
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("username",name);
                    jsonObject.put("password",password);
                    jsonObject.put("gcmid",regId);
                    jsonObject.put("action_id",0);
                    jsonObject.put("ad_id", t.getDeviceId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                paramss.put("json", jsonObject.toString());
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
                h.sendEmptyMessage(0);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);

    }


    public void submit(View v) {
         name = user.getText().toString();
         password = pass.getText().toString();
        if(name==null||name.equals(""))return;
        spinner.setVisibility(View.VISIBLE);
        user.setEnabled(false);
        pass.setEnabled(false);
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
                    pregister(Author.this, regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    h.sendEmptyMessage(0);
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = store.edit();
                editor.putString("device_regid", regid);
                editor.apply();
            }
        }.execute(null, null, null);

    }
    public void toast(){
        spinner.setVisibility(View.GONE);
        user.setEnabled(true);
        pass.setEnabled(true);
        Toast.makeText(Author.this,"Authentication failed",Toast.LENGTH_SHORT).show();
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
            CharSequence charSequence="status_code";

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    if(line.contains(charSequence))
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
                JSONObject js=new JSONObject(line);
                if (js.getInt("status_code")==1){
                    SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = store.edit();
                    editor.putString("usertext", name);
                    editor.putString("user_id", js.getInt("user_id") + "");
                    editor.apply();
                    finish();
                    startActivity(i);
                    return;
                }
                else h.sendEmptyMessage(0);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

}
