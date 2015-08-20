package com.barebringer.testgcm1;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

public class GCMMessagerHandler extends IntentService {

    String mes;
    private Handler handler;

    public GCMMessagerHandler() {
        super("GCMMessagerHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        mes = extras.getString("data");
        if(mes==null)return;
        SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
        String temp=store.getString("temp",null);
        String dtag=store.getString("defTags",null);
        try {
            JSONObject o=new JSONObject(mes);
            if(o==null)return;
            String mtag=o.getString("tags");
            String[] dl=dtag.split("/");
            String[] ml=mtag.split("/");
            int i,flag,j;
            for(i=0;i<dl.length;i++){
                String[] dval=dl[i].split("-");
                String[] mval=ml[i].split("-");
                flag=0;
                for(j=0;j<mval.length;j++){
                    if(mval[j].equals(dval[0]))flag=1;
                }
                if(flag==0)return;
            }
            generateNotification(getApplicationContext(), mes);
            GCMBroadcastReceiver.completeWakefulIntent(intent);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        if(temp!=null){
            if(temp.equals(mes))return;
        }
        SharedPreferences.Editor editor = store.edit();
        editor.putString("temp", mes);
        editor.apply();
        MyDBHandler d = new MyDBHandler(getApplicationContext(), null, null, 1);
        d.addName(mes);
        d.close();
    }

    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.plane;
        try {
            JSONObject j = new JSONObject(message);
            long when = System.currentTimeMillis();
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification;
            notification = new Notification(icon, "Nitt Calendar¬", when);
            String title = j.getString("username");

            Intent notificationIntent = new Intent(context, MainActivity.class);
            // set intent so it does not start a new activity
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent =
                    PendingIntent.getActivity(context, 0, notificationIntent, 0);
            notification.setLatestEventInfo(context, title, j.getString("message"), intent);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            // Play default notification sound
            notification.defaults |= Notification.DEFAULT_SOUND;

            // Vibrate if vibrate is enabled
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(0, notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
