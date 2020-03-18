package com.example.jobsrecruiter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jobsrecruiter.R;

public class ApplycandidateViewHolder extends RecyclerView.ViewHolder {

    public TextView candidateid,candidatedate,candidatename,candidatephone,candidateemail,candidateresume,candidateappliedfor;
    public ImageView candidateimage;

    public ApplycandidateViewHolder(View itemView) {
        super(itemView);

        candidateid=itemView.findViewById(R.id.candidate_id);
        candidatedate=itemView.findViewById(R.id.candidate_date);
        candidatename=itemView.findViewById(R.id.candidate_name);
        candidatephone=itemView.findViewById(R.id.candidate_phone);
        candidateemail=itemView.findViewById(R.id.candidate_email);
        candidateresume=itemView.findViewById(R.id.candidate_resume);
        candidateappliedfor=itemView.findViewById(R.id.candidate_appliedfor);
        candidateimage=itemView.findViewById(R.id.candidate_image);

    }
}
