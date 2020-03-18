package com.example.jobsrecruiter.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import com.example.jobsrecruiter.Model.Recruiter;
import com.example.jobsrecruiter.Remote.APIService;
import com.example.jobsrecruiter.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Retrofit;

public class Common {

    public static Recruiter currentRecruiter;

    public static final int PICK_IMAGE_REQUEST=71;

    public static final String fcmUrl="https://fcm.googleapis.com/";

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


    public static String getDate(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString());
        return date.toString();
    }
    public static APIService getFCMClient(){
        return RetrofitClient.getClient(fcmUrl).create(APIService.class);
    }
}