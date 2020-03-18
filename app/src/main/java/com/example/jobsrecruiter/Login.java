package com.example.jobsrecruiter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobsrecruiter.Common.Common;
import com.example.jobsrecruiter.Model.Recruiter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class Login extends AppCompatActivity {
    MaterialEditText edtPhone,edtPassword;
    FButton FLogin;
    ImageView logbackpress;
    CheckBox ckbRemember;
    TextView regtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtPhone=findViewById(R.id.edtphone);
        edtPassword=findViewById(R.id.edtpassw);
        FLogin=findViewById(R.id.Login);
        logbackpress=findViewById(R.id.logbackpress);
        ckbRemember=findViewById(R.id.ckbremember);
        regtext=findViewById(R.id.regtext);
        regtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
            }
        });

        //Init Paper
        Paper.init(this);

        //Init Firebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_candidate=database.getReference("Recruiter");
        FLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //save user and password
                if (ckbRemember.isChecked())
                {
                    Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                    Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
                }

                final ProgressDialog mDialog=new ProgressDialog(Login.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                table_candidate.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check if user exist or not in database
                        if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            //Get candidate information
                            mDialog.dismiss();
                            Recruiter recruiter=dataSnapshot.child(edtPhone.getText().toString()).getValue(Recruiter.class);
                            if (recruiter.getPassword().equals(edtPassword.getText().toString())) {
                                Intent intent=new Intent(Login.this,Home.class);
                                Common.currentRecruiter=recruiter;
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            mDialog.dismiss();
                            Toast.makeText(Login.this, "Please registered first!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        logbackpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,MainActivity.class));
            }
        });
    }
}
