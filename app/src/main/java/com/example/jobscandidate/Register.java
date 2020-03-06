package com.example.jobscandidate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.jobscandidate.Common.Common;
import com.example.jobscandidate.Model.Candidate;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class Register extends AppCompatActivity {

    MaterialEditText edtName,edtEmail,edtPhone,edtPassw;
    FButton FRegister;
    ImageView regbackpress;


    String resume="";
    String image="";

    RelativeLayout rootlayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rootlayout=findViewById(R.id.registerrootlayout);

        edtName=findViewById(R.id.edtregname);
        edtEmail=findViewById(R.id.edtregemail);
        edtPhone=findViewById(R.id.edtregphone);
        edtPassw=findViewById(R.id.edtregpassw);
        FRegister=findViewById(R.id.Register);
        regbackpress=findViewById(R.id.regbackpress);

        //Init Firebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_candidate=database.getReference("Candidate");


        FRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(Register.this);
                    mDialog.setMessage("Please waiting...");
                    mDialog.show();

                    table_candidate.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(Register.this, "This user already registered!", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                Candidate candidate = new Candidate(edtEmail.getText().toString(), edtName.getText().toString(), edtPassw.getText().toString(), edtPhone.getText().toString(), resume, image);
                                table_candidate.child(edtPhone.getText().toString()).setValue(candidate);
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                                Toast.makeText(Register.this, "Account created!...Login now!", Toast.LENGTH_SHORT).show();
                                finish();
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
        });

        regbackpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,MainActivity.class));
            }
        });


    }
}