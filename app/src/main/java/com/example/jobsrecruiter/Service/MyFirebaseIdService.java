package com.example.jobsrecruiter.Service;

import android.media.session.MediaSession;

import com.example.jobsrecruiter.Common.Common;
import com.example.jobsrecruiter.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        updateToServer(refreshToken);
    }

    private void updateToServer(String refreshToken) {
        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(refreshToken,true);
        tokens.child(Common.currentRecruiter.getPhone()).setValue(data);
    }
}
