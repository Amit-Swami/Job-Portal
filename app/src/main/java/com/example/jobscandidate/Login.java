package com.example.jobscandidate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobscandidate.Common.Common;
import com.example.jobscandidate.Model.Candidate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    MaterialEditText edtPhone, edtPassword;
    FButton FLogin;
    ImageView logbackpress;
    CheckBox ckbRemember;
    TextView registertext;

    RelativeLayout rootlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rootlayout=findViewById(R.id.loginrootlayout);


        edtPhone = findViewById(R.id.edtphone);
        edtPassword = findViewById(R.id.edtpassw);
        FLogin = findViewById(R.id.Login);
        logbackpress = findViewById(R.id.logbackpress);
        ckbRemember=findViewById(R.id.ckbremember);
        registertext=findViewById(R.id.registertext);
        registertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
            }
        });


        //Init Paper
        Paper.init(this);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_candidate = database.getReference("Candidate");
        FLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    //save user and password
                    if (ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }

                    final ProgressDialog mDialog = new ProgressDialog(Login.this);
                    mDialog.setMessage("Please waiting...");
                    mDialog.show();

                    table_candidate.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check if user exist or not in database
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                //Get candidate information
                                mDialog.dismiss();
                                Candidate candidate = dataSnapshot.child(edtPhone.getText().toString()).getValue(Candidate.class);
                                if (candidate.getPassword().equals(edtPassword.getText().toString())) {
                                    Intent intent = new Intent(Login.this, Home.class);
                                    Common.currentCandidate = candidate;
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(Login.this, "Please registered first!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });

        logbackpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        });
    }

}