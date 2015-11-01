package com.barebringer.testgcm1;

public final class CommonUtilities {

    // give your server registration url here
    static final String SERVER_URL = "http://beerfactory.pragyan.org/~kousik/campuscomm/register.php";
    static final String NEW_URL = "http://beerfactory.pragyan.org/~kousik/campuscomm/message.php";

    // Google project id
    static final String PROJECT_NUMBER = "835229264934";

    static final String TAG = "Nitt";

    static boolean start1 = false, start2 = false, start3 = false, apprun = false;
    //apprun = true if app is running
    //start1 = true if app is running and until all old messages are loaded in NITpost fragment
    //start2 = true if app is running and until all old messages are loaded in Fest fragment
    //start3 = true if app is running and until all old messages are loaded in Director fragment
    //basically start1,start2,start3 are used to avoid multiple loading of data
}
