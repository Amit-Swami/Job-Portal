package com.example.jobscandidate;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.jobscandidate.Common.Common;
import com.example.jobscandidate.Database.Database;
import com.example.jobscandidate.Helper.RecyclerItemTouchHelper;
import com.example.jobscandidate.Interface.RecyclerItemTouchHelperListener;
import com.example.jobscandidate.Model.Savedjob;
import com.example.jobscandidate.ViewHolder.SavedjobAdapter;
import com.example.jobscandidate.ViewHolder.SavedjobViewHolder;

public class SavedjobActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    SavedjobAdapter adapter;
    RelativeLayout rootLayout;
    ImageView imageback;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedjob);

        swipeRefreshLayout=findViewById(R.id.swipe_layoutsaved);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadSavedJob();
                }
                else {
                    Snackbar.make(rootLayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadSavedJob();
                }
                else {
                    Snackbar.make(rootLayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });

        imageback=findViewById(R.id.logbackpresssavedjob);
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rootLayout=findViewById(R.id.savedjob_rootlayout);

        recyclerView=findViewById(R.id.recycler_savedjob);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

    }

    private void loadSavedJob() {
        adapter=new SavedjobAdapter(this,new Database(this).getAllSavedJobs(Common.currentCandidate.getPhone()));
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SavedjobViewHolder)
        {
           // String name=((SavedjobAdapter)recyclerView.getAdapter()).getItem(position).getSavedTitle();

            final Savedjob deleteItem=((SavedjobAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());
            new Database(getBaseContext()).removeFromSavedjob(deleteItem.getJobId(), Common.currentCandidate.getPhone());

            Snackbar snackbar=Snackbar.make(rootLayout,"This job successfully removed from the Saved Job list",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToSavedjob(deleteItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}