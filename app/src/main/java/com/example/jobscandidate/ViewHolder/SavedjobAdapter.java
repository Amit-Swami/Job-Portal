package com.example.jobscandidate.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jobscandidate.Interface.ItemClickListener;
import com.example.jobscandidate.Jobdetail;
import com.example.jobscandidate.Joblist;
import com.example.jobscandidate.Model.Jobs;
import com.example.jobscandidate.Model.Savedjob;
import com.example.jobscandidate.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SavedjobAdapter extends RecyclerView.Adapter<SavedjobViewHolder> {

    private Context context;
    private List<Savedjob> savedjobList;

    public SavedjobAdapter(Context context, List<Savedjob> savedjobList) {
        this.context = context;
        this.savedjobList = savedjobList;
    }

    @Override
    public SavedjobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.savedjob_item,parent,false);
        return new SavedjobViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SavedjobViewHolder viewHolder, int position) {

        viewHolder.job_title.setText(savedjobList.get(position).getSavedTitle());
        viewHolder.job_compname.setText(savedjobList.get(position).getSavedCompanyName());
        viewHolder.job_exp.setText(savedjobList.get(position).getSavedExperience());
        viewHolder.job_loc.setText(savedjobList.get(position).getSavedLocation());
        viewHolder.job_skills.setText(savedjobList.get(position).getSavedSkills());
        Picasso.with(context).load(savedjobList.get(position).getSavedCompanyImage())
                .into(viewHolder.job_image);

        final Savedjob local=savedjobList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent jobdetail=new Intent(context, Jobdetail.class);
                jobdetail.putExtra("jobId",savedjobList.get(position).getJobId());
                context.startActivity(jobdetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return savedjobList.size();
    }

    public void removeItem(int position)
    {
        savedjobList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Savedjob item,int position)
    {
        savedjobList.add(position,item);
        notifyItemInserted(position);
    }

    public Savedjob getItem(int position)
    {
        return savedjobList.get(position);
    }
}