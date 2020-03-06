package com.example.jobscandidate.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import com.example.jobscandidate.Model.Candidate;
import com.example.jobscandidate.Remote.APIService;
import com.example.jobscandidate.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Retrofit;

public class Common {

    public static Candidate currentCandidate;
    private static final String BASE_URL="https://fcm.googleapis.com/";

    public static String PHONE_TEXT ="userPhone";

    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";

    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
            {
                for (int i=0;i<info.length;i++)
                {
                if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    return true;
            }
            }
        }
        return false;
    }


    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}