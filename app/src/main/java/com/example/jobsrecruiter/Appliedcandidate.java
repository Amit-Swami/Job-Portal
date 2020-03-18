package com.example.jobsrecruiter;

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

import com.example.jobsrecruiter.Common.Common;
import com.example.jobsrecruiter.Model.Applyrequest;
import com.example.jobsrecruiter.ViewHolder.ApplycandidateViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Appliedcandidate extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Applyrequest, ApplycandidateViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;

    ImageView imageback;

    RelativeLayout rootlayout;

    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliedcandidate);

        swipeRefreshLayout=findViewById(R.id.swipe_applycanlayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadcandidates();
                }
                else {
                    Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadcandidates();
                }
                else {
                    Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });

        rootlayout=findViewById(R.id.applycanrootlayout);

        imageback=findViewById(R.id.logbackpressac);
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db=FirebaseDatabase.getInstance();
        requests=db.getReference("Applyjobs");

        recyclerView=findViewById(R.id.recycler_candidate);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void loadcandidates() {

        FirebaseRecyclerOptions<Applyrequest> options=new FirebaseRecyclerOptions.Builder<Applyrequest>()
                .setQuery(requests,Applyrequest.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Applyrequest, ApplycandidateViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ApplycandidateViewHolder viewHolder, int position, @NonNull Applyrequest model) {
                viewHolder.candidateid.setText(adapter.getRef(position).getKey());
                viewHolder.candidatedate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                viewHolder.candidatename.setText(model.getName());
                viewHolder.candidatephone.setText(model.getPhone());
                viewHolder.candidateemail.setText(model.getEmail());
                viewHolder.candidateresume.setText(model.getResume());
                viewHolder.candidateappliedfor.setText(model.getAppliedfor());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.candidateimage);
            }

            @Override
            public ApplycandidateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.apply_candidate_layout,parent,false);
                return new ApplycandidateViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
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