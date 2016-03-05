package com.delta.campuscomm;

public final class CommonUtilities {

    // give your server registration url here
    static final String SERVER_URL = "http://hostel.nitt.edu/~kousik/campuscomm/register.php";
    static final String NEW_URL = "http://hostel.nitt.edu/~kousik/campuscomm/message.php";

    // Google project id
    static final String PROJECT_NUMBER = "835229264934";

    static final String TAG = "CAMPUSCOMM";

    static boolean isFetchNew = false;
    static boolean isFetchOld = false;
    static boolean isAppRun = false;

    //TODO Remove these once api is working
    //All Dummy JSON Snippets
    static final String messagesJSON = "{\n" +
            "  \"status\": 200,\n" +
            "  \"data\": {\n" +
            "    \"description\": \"Successfully fetched new messages!\",\n" +
            "    \"no_of_messages\": 25,\n" +
            "    \"messages\": [\n" +
            "      {\n" +
            "        \"id\": \"160\",\n" +
            "        \"Message\": \"Testing#5\",\n" +
            "        \"Sender\": \"director\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"15\\\",\\\"16\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-13 17:49:23\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"161\",\n" +
            "        \"Message\": \"ffj\",\n" +
            "        \"Sender\": \"director\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-13 18:23:21\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"162\",\n" +
            "        \"Message\": \"vfjbcdg\",\n" +
            "        \"Sender\": \"director\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-13 18:26:28\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"163\",\n" +
            "        \"Message\": \"Testing#66\",\n" +
            "        \"Sender\": \"director\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"15\\\",\\\"16\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-13 18:29:41\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"164\",\n" +
            "        \"Message\": \"jsjsns\",\n" +
            "        \"Sender\": \"106114045\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\",\\\"ece\\\"],\\\"year\\\":[\\\"15\\\",\\\"14\\\",\\\"16\\\"],\\\"degree\\\":[\\\"mtech\\\",\\\"btech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-13 18:36:22\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"165\",\n" +
            "        \"Message\": \"Testing#66\",\n" +
            "        \"Sender\": \"director\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"15\\\",\\\"16\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-13 18:36:58\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"166\",\n" +
            "        \"Message\": \"fghh\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:16:51\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"167\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"degree\\\":\\\"btech,mtech\\\", \\\"year\\\":\\\"14\\\", \\\"dept\\\":\\\"cse,ece\\\"}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:22:52\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"168\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"degree\\\":\\\"btech,mtech\\\", \\\"year\\\":\\\"14\\\", \\\"dept\\\":\\\"cse,ece\\\"}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:25:05\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"169\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"degree\\\":\\\"btech,mtech,\\\", \\\"year\\\":\\\"14,\\\", \\\"dept\\\":\\\"cse,ece,\\\"}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:25:40\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"170\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:34:13\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"171\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:34:15\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"172\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:35:37\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"173\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:35:38\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"174\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:36:52\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"175\",\n" +
            "        \"Message\": \"addsadASAS\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:37:01\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"176\",\n" +
            "        \"Message\": \"addsadASAS\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:37:50\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"177\",\n" +
            "        \"Message\": \"addsadASAS\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:37:51\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"178\",\n" +
            "        \"Message\": \"addsadASAS\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:38:21\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"179\",\n" +
            "        \"Message\": \"addsadASAS\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:38:42\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"180\",\n" +
            "        \"Message\": \"addsadASAS\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:39:27\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"181\",\n" +
            "        \"Message\": \"addsadASAS\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:39:40\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"182\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:42:27\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"183\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:43:21\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"184\",\n" +
            "        \"Message\": \"dsajkkj\",\n" +
            "        \"Sender\": \"106114088\",\n" +
            "        \"tags\": \"{\\\"dept\\\":[\\\"cse\\\"],\\\"year\\\":[\\\"16\\\",\\\"15\\\",\\\"14\\\"],\\\"degree\\\":[\\\"btech\\\",\\\"mtech\\\"]}\",\n" +
            "        \"view_count\": \"0\",\n" +
            "        \"created_at\": \"2016-01-14 10:45:19\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

}
