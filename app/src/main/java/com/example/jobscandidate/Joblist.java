package com.example.jobscandidate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobscandidate.Common.Common;
import com.example.jobscandidate.Database.Database;
import com.example.jobscandidate.Interface.ItemClickListener;
import com.example.jobscandidate.Model.Categorymodel;
import com.example.jobscandidate.Model.Jobs;
import com.example.jobscandidate.Model.Savedjob;
import com.example.jobscandidate.ViewHolder.JobsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class Joblist extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference jobList;

    String categoryId="";

    FirebaseRecyclerAdapter<Jobs, JobsViewHolder> adapter;

    //Savedjob
    Database localDB;

    RelativeLayout rootlayout;
    ImageView imageViewback;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joblist);

        swipeRefreshLayout=findViewById(R.id.swipe_joblistlayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Get intent here
                if (getIntent()!=null)
                    categoryId=getIntent().getStringExtra("categoryId");
                if (!categoryId.isEmpty() && categoryId!=null)
                {
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadjoblist(categoryId);
                    }
                    else {
                        Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get intent here
                if (getIntent()!=null)
                    categoryId=getIntent().getStringExtra("categoryId");
                if (!categoryId.isEmpty() && categoryId!=null)
                {
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadjoblist(categoryId);
                    }
                    else {
                        Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
            }
        });

        imageViewback=findViewById(R.id.logbackpressjoblist);
        imageViewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //init Firebase
        database=FirebaseDatabase.getInstance();
        jobList=database.getReference("Jobs");

        //LOcal DB
        localDB=new Database(this);

        rootlayout=findViewById(R.id.joblistrootlayout);

        recyclerView=findViewById(R.id.recycler_job);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void loadjoblist(String categoryId) {
        Query jobListByCategoryId=jobList.orderByChild("categoryId").equalTo(categoryId);
        FirebaseRecyclerOptions<Jobs> options=new FirebaseRecyclerOptions.Builder<Jobs>()
                .setQuery(jobListByCategoryId,Jobs.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Jobs, JobsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final JobsViewHolder viewHolder, final int position, @NonNull final Jobs model) {
                viewHolder.job_title.setText(model.getTitle());
                viewHolder.job_compname.setText(model.getCompanyName());
                viewHolder.job_exp.setText(model.getExperience());
                viewHolder.job_loc.setText(model.getLocation());
                viewHolder.job_skills.setText(model.getSkills());
                Picasso.with(getBaseContext()).load(model.getCompanyImage())
                        .into(viewHolder.job_image);

                //Add savedjob
                if (localDB.isSavedjob(adapter.getRef(position).getKey(), Common.currentCandidate.getPhone()))
                    viewHolder.savedjob_image.setImageResource(R.drawable.ic_star_black_24dp);

                //click to change state of savedjob
                viewHolder.savedjob_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Savedjob savedjob=new Savedjob();
                        savedjob.setJobId(adapter.getRef(position).getKey());
                        savedjob.setSavedTitle(model.getTitle());
                        savedjob.setSavedCompanyName(model.getCompanyName());
                        savedjob.setSavedCompanyImage(model.getCompanyImage());
                        savedjob.setSavedExperience(model.getExperience());
                        savedjob.setSavedLocation(model.getLocation());
                        savedjob.setSavedSkills(model.getSkills());
                        savedjob.setSavedVacancies(model.getVacancies());
                        savedjob.setSavedWalkinTnV(model.getWalkinTnV());
                        savedjob.setSavedSalary(model.getSalary());
                        savedjob.setSavedPostDate(model.getPostDate());
                        savedjob.setSavedJobDescription(model.getJobDescription());
                        savedjob.setSavedIndustryType(model.getIndustryType());
                        savedjob.setSavedFunctionalArea(model.getFunctionalArea());
                        savedjob.setSavedJobRole(model.getJobRole());
                        savedjob.setSavedEmploymentType(model.getEmploymentType());
                        savedjob.setSavedDesiredProfile(model.getDesiredProfile());
                        savedjob.setSavedCompanyWebsite(model.getCompanyWebsite());
                        savedjob.setSavedCompanyDescription(model.getCompanyDescription());
                        savedjob.setSavedHrName(model.getHrName());
                        savedjob.setSavedHrContact(model.getHrContact());
                        savedjob.setSavedCategoryId(model.getCategoryId());
                        savedjob.setUserPhone(Common.currentCandidate.getPhone());


                        if (!localDB.isSavedjob(adapter.getRef(position).getKey(),Common.currentCandidate.getPhone()))
                        {
                            localDB.addToSavedjob(savedjob);
                            viewHolder.savedjob_image.setImageResource(R.drawable.ic_star_black_24dp);
                            Snackbar.make(rootlayout,"Job Saved Successfully!!",Snackbar.LENGTH_LONG).show();
                        }
                        else
                        {
                            localDB.removeFromSavedjob(adapter.getRef(position).getKey(),Common.currentCandidate.getPhone());
                            viewHolder.savedjob_image.setImageResource(R.drawable.ic_star_border_black_24dp);
                        }
                    }
                });

                final  Jobs local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent jobdetail=new Intent(Joblist.this,Jobdetail.class);
                        jobdetail.putExtra("jobId",adapter.getRef(position).getKey());
                        startActivity(jobdetail);
                    }
                });
            }

            @Override
            public JobsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item,parent,false);
                return new JobsViewHolder(itemView);
            }
        };
        adapter.startListening();
        //set adapter
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}