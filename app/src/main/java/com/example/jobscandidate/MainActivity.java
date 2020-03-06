package com.example.jobscandidate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobscandidate.Common.Common;
import com.example.jobscandidate.Model.Candidate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tomer.fadingtextview.FadingTextView;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

import static java.lang.Thread.*;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    FButton btnRegister,btnLogin;
    FadingTextView title;

    RelativeLayout rootlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        title = findViewById(R.id.headline);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                title.stop();
            }
        }, 7000);


        btnRegister=findViewById(R.id.btnRegister);
        btnLogin=findViewById(R.id.btnLogin);

        //Init paper
        Paper.init(this);

        rootlayout=findViewById(R.id.mainrootlayout);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, Login.class));

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,Register.class));
            }
        });

        //Check remember
        String user=Paper.book().read(Common.USER_KEY);
        String pwd=Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd != null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
                login(user,pwd);
        }
    }

    private void login(final String phone, final String pwd) {

        if (Common.isConnectedToInternet(getBaseContext())) {

            //Init Firebase
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference table_candidate = database.getReference("Candidate");

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please waiting...");
            mDialog.show();

            table_candidate.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //check if user exist or not in database
                    if (dataSnapshot.child(phone).exists()) {
                        //Get candidate information
                        mDialog.dismiss();
                        Candidate candidate = dataSnapshot.child(phone).getValue(Candidate.class);
                        if (candidate.getPassword().equals(pwd)) {
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            Common.currentCandidate = candidate;
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Please registered first!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
            return;
        }
    }
}