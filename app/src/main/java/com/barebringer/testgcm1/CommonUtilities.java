package com.barebringer.testgcm1;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public final class CommonUtilities {

    // give your server registration url here
    static final String SERVER_URL = "http://87eeeb00.ngrok.io/insertuser.php";
    static final String POST_URL = "http://87eeeb00.ngrok.io/sendall.php";
    static final String NEW_URL = "http://87eeeb00.ngrok.io/temp.php";

    // Google project id
    static final String SENDER_ID = "835229264934";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.androidhive.pushnotifications.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    static ArrayList<String> level1=new ArrayList<String>(){
        {add("STUDENT-");add("NEXT LEVEL");}
    };
    static ArrayList<String> level2=new ArrayList<String>(){
        {add("BTECH-");add("MTECH-");add("NEXT LEVEL");}
    };
    static ArrayList<String> level3=new ArrayList<String>(){
        {add("1-");add("2-");add("3-");add("4-");add("5-");add("NEXT LEVEL");}
    };
    static ArrayList<String> level4=new ArrayList<String>(){
        {add("ARCHI-");add("CHEMICAL-");add("CIVIL-");add("CSE-");add("ECE-");add("EEE-");add("ICE-");add("MECH-");add("META-");add("PROD-");add("DONE");}
    };
}
