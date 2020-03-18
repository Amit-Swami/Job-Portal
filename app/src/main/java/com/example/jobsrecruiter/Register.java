package com.example.jobsrecruiter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jobsrecruiter.Model.Recruiter;
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
    String image="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtName=findViewById(R.id.edtregname);
        edtEmail=findViewById(R.id.edtregemail);
        edtPhone=findViewById(R.id.edtregphone);
        edtPassw=findViewById(R.id.edtregpassw);
        FRegister=findViewById(R.id.Register);
        regbackpress=findViewById(R.id.regbackpress);

        //Init Firebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_recruiter=database.getReference("Recruiter");

        FRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog=new ProgressDialog(Register.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                table_recruiter.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(edtPhone.getText().toString()).exists()){
                            mDialog.dismiss();
                            Toast.makeText(Register.this, "This user already registered!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            mDialog.dismiss();
                            Recruiter recruiter=new Recruiter(edtEmail.getText().toString(),edtName.getText().toString(),edtPassw.getText().toString(),edtPhone.getText().toString(),image);
                            table_recruiter.child(edtPhone.getText().toString()).setValue(recruiter);
                            Intent intent=new Intent(Register.this,Login.class);
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
        });

        regbackpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,MainActivity.class));
            }
        });
    }
}