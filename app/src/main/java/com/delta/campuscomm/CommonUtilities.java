package com.delta.campuscomm;

public final class CommonUtilities {

    // give your server registration url here
    static final String REGISTER_URL = "http://hostel.nitt.edu/campuscomm/register";
    static final String SEND_URL = "http://hostel.nitt.edu/campuscomm/send";
    static final String NEW_URL = "http://hostel.nitt.edu/campuscomm/fetchNew";
    static final String OLD_URL = "http://hostel.nitt.edu/campuscomm/fetchOld";

    // Google project id
    static final String PROJECT_NUMBER = "835229264934";

    static final String TAG = "GlobalTag";

    static boolean isFetchNew = false;
    static boolean isFetchOld = false;
    static boolean isViewUpdate = false;
    static boolean isAppRun = false;

    static MyDBHandler myDBHandler;

}
