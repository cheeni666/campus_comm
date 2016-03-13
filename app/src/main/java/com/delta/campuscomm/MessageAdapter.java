package com.delta.campuscomm;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.delta.campuscomm.CommonUtilities.*;


public class MessageAdapter extends ArrayAdapter<String> {
    Random random = new Random();
    Integer ID = -1, statusCode;
    String  viewString = "";

    public MessageAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.layout_message_adapter, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customview = layoutInflater.inflate(R.layout.layout_message_adapter, parent, false);

        String item = getItem(position);
        Calendar calendar = Calendar.getInstance();

        //Generate the post for the list view
        TextView textViewUsername = (TextView) customview.findViewById(R.id.textViewUsernameMessageAdapter);
        TextView textViewMessage = (TextView) customview.findViewById(R.id.textViewMessageMessageAdapter);
        TextView textViewTime = (TextView) customview.findViewById(R.id.textViewTimeMessageAdapter);
        ImageView imageViewStrip = (ImageView) customview.findViewById(R.id.imageViewStripMessageAdapter);
        TextView textViewTags = (TextView) customview.findViewById(R.id.textView_tags);
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        imageViewStrip.setBackgroundColor(color);

        Button buttonViews = (Button)customview.findViewById(R.id.buttonViewsMessageAdapter);

        try {
            JSONObject jsonItem = new JSONObject(item);
            textViewUsername.setText(jsonItem.getString("Sender") + " posted");
            textViewMessage.setText(jsonItem.getString("Message"));
            String tagsString = jsonItem.getString("tags");
            ID = Integer.parseInt(jsonItem.getString("id"));

            buttonViews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonAction();
                }
            });

            JSONObject tagsObject = new JSONObject(tagsString);
            JSONArray deptArray = tagsObject.getJSONArray("dept");
            JSONArray yearArray = tagsObject.getJSONArray("year");
            JSONArray degreeArray = tagsObject.getJSONArray("degree");
            ArrayList<String> tags = new ArrayList<>();
            for(int i=0;i<deptArray.length();i++)
                tags.add(deptArray.getString(i));
            for(int i=0;i<yearArray.length();i++)
                tags.add(yearArray.getString(i));
            for(int i=0;i<degreeArray.length();i++)
                tags.add(degreeArray.getString(i));
            //Processing timestamp
            String[] timestamp = jsonItem.getString("created_at").split(" ");
            String[] date = timestamp[0].split("-");
            String[] time = timestamp[1].split(":");
            if (calendar.get(Calendar.YEAR) != Integer.parseInt(date[0])) {
                textViewTime.setText(timestamp[0]);
            } else if (1 + calendar.get(Calendar.MONTH) != Integer.parseInt(date[1])) {
                textViewTime.setText(timestamp[0]);
            } else if (calendar.get(Calendar.DAY_OF_MONTH) != Integer.parseInt(date[2])) {
                int t = calendar.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(date[2]);
                if (t == 1) textViewTime.setText("yesterday");
                else textViewTime.setText(timestamp[0]);
            } else {
                textViewTime.setText("today at " + time[0] + ":" + time[1]);
            }

            for(int i=0;i<tags.size();i++)
                textViewTags.append(tags.get(i).toUpperCase()+", ");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return customview;
    }

    public void buttonAction(){
        if(isViewUpdate)Toast.makeText(getContext(), "Service Busy", Toast.LENGTH_SHORT).show();
        isViewUpdate = true;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String serverUrl = OLD_URL;
                statusCode = -1;
                Integer oldId = 1 + ID;
                //old_id gets the oldest message id loaded
                if(oldId == 1)return null;

                Map<String, String> paramss = new HashMap<String, String>();
                paramss.put("oldest_msg_id", oldId + "");
                paramss.put("no_of_messages", "1");

                try {
                    post(serverUrl, paramss);
                } catch (IOException e) {
                    Log.d(TAG, "" + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String msg) {
                isViewUpdate = false;
                if(statusCode != 200)
                        Toast.makeText(getContext(), "Failed To get View count", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getContext(), viewString, Toast.LENGTH_SHORT).show();
                }

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

            String tmpline = "", response = "";
            try {
                while ((tmpline = reader.readLine()) != null) {
                    response += tmpline;
                }
                JSONObject jsonResponse = new JSONObject(response);
                Log.d(TAG, response);
                statusCode = jsonResponse.getInt("status");
                if(statusCode == 200){
                    viewString = jsonResponse.getJSONObject("data").
                                    getJSONArray("messages").getJSONObject(0).getString("view_count");
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