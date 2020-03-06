package com.example.jobscandidate.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jobscandidate.Interface.ItemClickListener;
import com.example.jobscandidate.R;

public class JobsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView job_title,job_compname,job_exp,job_loc,job_skills;
    public ImageView job_image,savedjob_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public JobsViewHolder(View itemView) {
        super(itemView);

        job_title=itemView.findViewById(R.id.job_title);
        job_compname=itemView.findViewById(R.id.job_compname);
        job_exp=itemView.findViewById(R.id.job_experience);
        job_loc=itemView.findViewById(R.id.job_location);
        job_skills=itemView.findViewById(R.id.job_skills);
        job_image=itemView.findViewById(R.id.job_image);
        savedjob_image=itemView.findViewById(R.id.job_savedjob);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}