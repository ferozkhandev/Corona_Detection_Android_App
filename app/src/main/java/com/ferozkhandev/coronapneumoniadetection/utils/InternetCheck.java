package com.ferozkhandev.coronapneumoniadetection.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetCheck {
    public static boolean netCheck(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm!=null){
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } else {
            Log.d("TestNet", "WIFI off");
            return false;
        }
    }
}
