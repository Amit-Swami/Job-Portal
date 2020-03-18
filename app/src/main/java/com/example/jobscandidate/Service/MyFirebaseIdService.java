package com.example.jobscandidate.Service;

import com.example.jobscandidate.Common.Common;
import com.example.jobscandidate.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        if (Common.currentCandidate != null)
            updateTokenToFirebase(refreshToken);
    }

    private void updateTokenToFirebase(String refreshToken) {
        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(refreshToken,false);
        tokens.child(Common.currentCandidate.getPhone()).setValue(data);
    }
}