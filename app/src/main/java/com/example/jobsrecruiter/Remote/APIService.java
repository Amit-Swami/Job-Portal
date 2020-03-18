package com.example.jobsrecruiter.Remote;

import com.example.jobsrecruiter.Model.MyResponse;
import com.example.jobsrecruiter.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAATqg_-14:APA91bFGEwDMCTdZFGkG6KvcZYJC9AG4pt2ZbscJxyhNyaD8le5ei4EN9QYeqAtqdlePSeOxtWnPSCZmT2MEpVBX4FyKeJan8h0FlCK5eBCAdV158_sE-HsREjnJipAulFLuGFs9bMBN"

            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
