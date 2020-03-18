package com.example.jobscandidate;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobscandidate.Common.Common;
import com.example.jobscandidate.Database.Database;
import com.example.jobscandidate.Model.Applyrequest;
import com.example.jobscandidate.Model.Jobs;
import com.example.jobscandidate.Model.MyResponse;
import com.example.jobscandidate.Model.Notification;
import com.example.jobscandidate.Model.Savedjob;
import com.example.jobscandidate.Model.Sender;
import com.example.jobscandidate.Model.Token;
import com.example.jobscandidate.Remote.APIService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Jobdetail extends AppCompatActivity {

    TextView jobd_title,jobd_compname,jobd_exp,jobd_loc,jobd_skills,jobd_vacancy,jobd_salary,jobd_postdate,
            jobd_walkin,jobd_descrip,jobd_industrytype,jobd_functarea,jobd_role,jobd_empltype,
    jobd_desiredcp,jobd_compname2,jobd_website,jobd_compdescript,jobd_hrname,jobd_hrcontact;

    ImageView jobd_image,imageback;
    FButton jobd_apply;

    String jobId="";
    FirebaseDatabase database,applydatabase;
    DatabaseReference jobs,applyrequests;

    String notmentioned="Not Mentioned";
    String notdisclosed="Not Disclosed";
    String walkintxt="Apply to job, No direct walk-in";

    RelativeLayout relative1;

    Jobs jobsmodel;

    APIService mService;

    String appliedfor="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobdetail);

        imageback=findViewById(R.id.logbackpressjobdetail);
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Init Service
        mService=Common.getFCMService();

        relative1=findViewById(R.id.relative1);

        database=FirebaseDatabase.getInstance();
        jobs=database.getReference("Jobs");

        applydatabase=FirebaseDatabase.getInstance();
        applyrequests=applydatabase.getReference("Applyjobs");

        jobsmodel=new Jobs();


        jobd_title=findViewById(R.id.jobd_title);
        jobd_compname=findViewById(R.id.jobd_compname);
        jobd_exp=findViewById(R.id.jobd_experience);
        jobd_loc=findViewById(R.id.jobd_location);
        jobd_skills=findViewById(R.id.jobd_skills);
        jobd_vacancy=findViewById(R.id.jobd_vacancy);
        jobd_salary=findViewById(R.id.jobd_salary);
        jobd_postdate=findViewById(R.id.jobd_postdate);
        jobd_walkin=findViewById(R.id.jobd_walkinTnD);
        jobd_descrip=findViewById(R.id.jobd_jobdescrip);
        jobd_industrytype=findViewById(R.id.jobd_industrytype);
        jobd_functarea=findViewById(R.id.jobd_functionalarea);
        jobd_role=findViewById(R.id.jobd_role);
        jobd_empltype=findViewById(R.id.jobd_employtype);
        jobd_desiredcp=findViewById(R.id.jobd_desiredcp);
        jobd_compname2=findViewById(R.id.jobd_compname2);
        jobd_website=findViewById(R.id.jobd_website);
        jobd_compdescript=findViewById(R.id.jobd_compdescrip);
        jobd_hrname=findViewById(R.id.jobd_hrname);
        jobd_hrcontact=findViewById(R.id.jobd_hrcontact);
        jobd_image=findViewById(R.id.jobd_image);
        jobd_apply=findViewById(R.id.apply);

        //get intent
        if (getIntent() != null)
            jobId=getIntent().getStringExtra("jobId");
        if (!jobId.isEmpty())
        {
            if (Common.isConnectedToInternet(getBaseContext())) {
                getJobDetail(jobId);
            }
            else {
                Snackbar.make(relative1,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                return;
            }
        }

        jobd_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Applyrequest applyrequest=new Applyrequest(
                        Common.currentCandidate.getName(),
                        Common.currentCandidate.getEmail(),
                        Common.currentCandidate.getPhone(),
                        jobId,
                        Common.currentCandidate.getResume(),
                        appliedfor,
                        Common.currentCandidate.getImage()
                );

                String interviewee_req=String.valueOf(System.currentTimeMillis());
                applyrequests.child(interviewee_req)
                        .setValue(applyrequest);

                sendNotification(interviewee_req);


            }
        });

    }

    private void sendNotification(final String interviewee_req) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data=tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                    Token serverToken = postSnapshot.getValue(Token.class);

                    //Crete raw payload to send
                    Notification notification=new Notification("Job.com","You have new interviewee request "+interviewee_req);
                    Sender content=new Sender(serverToken.getToken(),notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().sucess == 1) {
                                            Snackbar.make(relative1, "You have successfully applied for this job !!", Snackbar.LENGTH_LONG).show();
                                            finish();
                                        } else {
                                            Snackbar.make(relative1, "You have successfully applied for this job !!", Snackbar.LENGTH_LONG).show();
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.d("ERROR",t.getMessage());

                                }
                            });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getJobDetail(final String jobId) {
        jobs.child(jobId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Jobs jobs=dataSnapshot.getValue(Jobs.class);

                //set Image
                Picasso.with(getBaseContext()).load(jobs.getCompanyImage())
                        .into(jobd_image);
                jobd_title.setText(jobs.getTitle());
                jobd_compname.setText(jobs.getCompanyName());
                jobd_exp.setText(jobs.getExperience());
                jobd_loc.setText(jobs.getLocation());
                jobd_skills.setText(jobs.getSkills());
                jobd_vacancy.setText(jobs.getVacancies());
                jobd_salary.setText(jobs.getSalary());
                jobd_postdate.setText(jobs.getPostDate());
                jobd_walkin.setText(jobs.getWalkinTnV());
                jobd_descrip.setText(jobs.getJobDescription());
                jobd_industrytype.setText(jobs.getIndustryType());
                jobd_functarea.setText(jobs.getFunctionalArea());
                jobd_role.setText(jobs.getJobRole());
                jobd_empltype.setText(jobs.getEmploymentType());
                jobd_desiredcp.setText(jobs.getDesiredProfile());
                jobd_compname2.setText(jobs.getCompanyName());
                jobd_website.setText(jobs.getCompanyWebsite());
                jobd_compdescript.setText(jobs.getCompanyDescription());
                jobd_hrname.setText(jobs.getHrName());
                jobd_hrcontact.setText(jobs.getHrContact());

                String jobd_vacancy2=jobd_vacancy.getText().toString();
                String jobd_salary2=jobd_salary.getText().toString();
                String jobd_walkin2=jobd_walkin.getText().toString();
                String jobd_hrname2=jobd_hrname.getText().toString();
                String jobd_hrcontact2=jobd_hrcontact.getText().toString();
                if (jobd_vacancy2.isEmpty())
                    jobd_vacancy.setText(notdisclosed);
                if (jobd_salary2.isEmpty())
                    jobd_salary.setText(notdisclosed);
                if (jobd_walkin2.isEmpty())
                    jobd_walkin.setText(walkintxt);
                if (jobd_hrname2.isEmpty())
                    jobd_hrname.setText(notmentioned);
                if (jobd_hrcontact2.isEmpty())
                    jobd_hrcontact.setText(notmentioned);

                appliedfor=jobd_compname.getText().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}