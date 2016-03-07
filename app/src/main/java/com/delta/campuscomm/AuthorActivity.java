package com.delta.campuscomm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import static com.delta.campuscomm.CommonUtilities.*;

public class AuthorActivity extends Activity {
    EditText editTextUsername, editTextPassword;
    GoogleCloudMessaging gcm;
    String regId = "";
    String username, password, token;
    Intent intent;

    int statusCode;

    ProgressBar spinner;
    TelephonyManager telephonyManager;

    Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        intent = new Intent(this, AllFunctionsActivity.class);

        buttonLogin = (Button)findViewById(R.id.button_login_author);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        editTextUsername = (EditText) findViewById(R.id.editText_userName_author);
        editTextPassword = (EditText) findViewById(R.id.editText_password_author);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Change
                //startActivity(new Intent(AuthorActivity.this,AllFunctionsActivity.class));
                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                if (username == null || username.equals("")) return;

                spinner.setVisibility(View.VISIBLE);
                editTextUsername.setEnabled(false);
                editTextPassword.setEnabled(false);

                new AsyncTask<Void, Void, String>() {
                    boolean isGcmSuccess = false;

                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            if (gcm == null) {
                                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                            }
                            regId = gcm.register(PROJECT_NUMBER);
                            isGcmSuccess = true;
                            setPostParams(AuthorActivity.this, regId);
                        } catch (IOException ex) {
                            Log.d(CommonUtilities.TAG, "GcmException: " + ex);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        //Write GCMid to the shared memory for future reference
                        SharedPreferences store = getSharedPreferences("campuscomm", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = store.edit();
                        editor.putString("gcmId", regId);
                        editor.apply();
                        if (!isGcmSuccess){
                            Toast.makeText(getApplicationContext(), "GCM Registraion failed!", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                            editTextUsername.setEnabled(true);
                            editTextPassword.setEnabled(true);
                        }
                    }
                }.execute(null, null, null);
            }
        });
    }

    void setPostParams(final Context context, final String regId) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                statusCode = 0;
                String serverUrl = REGISTER_URL;

                Map<String, String> paramss = new HashMap<String, String>();
                paramss.put("username", username);
                paramss.put("password", password);
                paramss.put("gcmid", regId);
                paramss.put("ad_id", telephonyManager.getDeviceId());

                try {
                    post(serverUrl, paramss);
                } catch (IOException e) {
                    Log.d(TAG, "Failed to Register in Server");
                }
                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //if login successfull go to next activity
                if (statusCode == 200) {
                    SharedPreferences store = getSharedPreferences("campuscomm", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = store.edit();
                    editor.putString("userName", username);
                    editor.putString("token", token);
                    editor.apply();
                    finish();
                    startActivity(intent);
                    return;
                }
                Toast.makeText(getApplicationContext(), "Failed Authorisation!", Toast.LENGTH_SHORT).show();
                spinner.setVisibility(View.GONE);
                editTextUsername.setEnabled(true);
                editTextPassword.setEnabled(true);
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
            Log.d("URL", "> " + url);
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
                JSONObject jsRespose = new JSONObject(response);
                Log.d(TAG, jsRespose.toString());
                statusCode = jsRespose.getInt("status");
                if (statusCode == 200) {
                    token = jsRespose.getJSONObject("data").getString("token");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, " " + e);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
