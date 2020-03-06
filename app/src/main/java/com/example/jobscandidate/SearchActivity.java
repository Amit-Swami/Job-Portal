package com.example.jobscandidate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.jobscandidate.Common.Common;
import com.example.jobscandidate.Database.Database;
import com.example.jobscandidate.Interface.ItemClickListener;
import com.example.jobscandidate.Model.Jobs;
import com.example.jobscandidate.Model.Savedjob;
import com.example.jobscandidate.ViewHolder.JobsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<Jobs, JobsViewHolder> searchAdapter;
    FirebaseRecyclerAdapter<Jobs, JobsViewHolder> adapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference jobList;

    Database localDB;

    RelativeLayout rootLayout;

    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        rootLayout=findViewById(R.id.searchrootlayout);
        swipeRefreshLayout=findViewById(R.id.swipe_searchlayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //load all jobs
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadAllJobs();
                }
                else
                {
                    Snackbar.make(rootLayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //load all jobs
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadAllJobs();
                }
                else
                {
                    Snackbar.make(rootLayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        //init Firebase
        database=FirebaseDatabase.getInstance();
        jobList=database.getReference("Jobs");

        //LOcal DB
        localDB=new Database(this);

        recyclerView=findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Search
        materialSearchBar=findViewById(R.id.searchbar);
        materialSearchBar.setHint("Search by Company");
        loadSuggest();
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //when user type their text , we will change suggest list

                List<String> suggest=new ArrayList<String>();
                for (String search:suggestList)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean b) {
                //when search Bar is close
                //Restore original suggest adapter
                if (!b)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence charSequence) {
                //when search finish
                //Show result of search adapter
                startSearch(charSequence);
            }

            @Override
            public void onButtonClicked(int i) {

            }
        });

    }

    private void loadAllJobs() {
        final Query searchByName=jobList;
        FirebaseRecyclerOptions<Jobs> productOptions=new FirebaseRecyclerOptions.Builder<Jobs>()
                .setQuery(searchByName,Jobs.class)
                .build();

        adapter= new FirebaseRecyclerAdapter<Jobs, JobsViewHolder>(productOptions) {
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
                            Snackbar.make(rootLayout,"Job Saved Successfully!!",Snackbar.LENGTH_LONG).show();
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
                        Intent jobdetail=new Intent(SearchActivity.this,Jobdetail.class);
                        jobdetail.putExtra("jobId",adapter.getRef(position).getKey());
                        startActivity(jobdetail);
                    }
                });
            }

            @NonNull
            @Override
            public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView=LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.job_item,viewGroup,false);
                return new JobsViewHolder(itemView);
            }
        };
        adapter.startListening();
        //set Adapter
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void startSearch(CharSequence charSequence) {
        Query searchByName=jobList.orderByChild("companyName").equalTo(charSequence.toString());
        FirebaseRecyclerOptions<Jobs> productOptions=new FirebaseRecyclerOptions.Builder<Jobs>()
                .setQuery(searchByName,Jobs.class)
                .build();

        searchAdapter= new FirebaseRecyclerAdapter<Jobs, JobsViewHolder>(productOptions) {
            @Override
            protected void onBindViewHolder(@NonNull JobsViewHolder viewHolder, int position, @NonNull Jobs model) {
                viewHolder.job_title.setText(model.getTitle());
                viewHolder.job_compname.setText(model.getCompanyName());
                viewHolder.job_exp.setText(model.getExperience());
                viewHolder.job_loc.setText(model.getLocation());
                viewHolder.job_skills.setText(model.getSkills());
                Picasso.with(getBaseContext()).load(model.getCompanyImage())
                        .into(viewHolder.job_image);

                final Jobs local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent productdetail=new Intent(SearchActivity.this,Jobdetail.class);
                        productdetail.putExtra("jobId",searchAdapter.getRef(position).getKey());
                        startActivity(productdetail);
                    }
                });
            }

            @NonNull
            @Override
            public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView= LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.job_item,viewGroup,false);
                return new JobsViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        jobList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Jobs item=postSnapshot.getValue(Jobs.class);
                            suggestList.add(item.getCompanyName());
                        }

                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        if (searchAdapter != null)
            searchAdapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (adapter != null)
            adapter.startListening();
        if (searchAdapter != null)
            searchAdapter.startListening();
        super.onResume();
    }
}