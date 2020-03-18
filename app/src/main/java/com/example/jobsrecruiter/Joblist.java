package com.example.jobsrecruiter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.jobsrecruiter.Common.Common;
import com.example.jobsrecruiter.Interface.ItemClickListener;
import com.example.jobsrecruiter.Model.Categorymodel;
import com.example.jobsrecruiter.Model.Jobs;
import com.example.jobsrecruiter.ViewHolder.JobViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Joblist extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    FirebaseDatabase database;
    DatabaseReference jobList;

    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";

    FirebaseRecyclerAdapter<Jobs, JobViewHolder> adapter;

    MaterialEditText edtTitle,edtCompname,edtExp,edtLoc,edtSkills,edtVacancy,edtWalkin,edtSalary,edtJobdescript,edtIndustype,
    edtFuncarea,edtJobrole,edtEmptype,edtDesiredcp,edtCompweb,edtCompdescript,edtHrname,edtHrcontact;

    String sedtTitle,sedtCompname,sedtExp,sedtLoc,sedtSkills,sedtVacancy,sedtWalkin,sedtSalary,sedtJobdescript,sedtIndustype,
            sedtFuncarea,sedtJobrole,sedtEmptype,sedtDesiredcp,sedtCompweb,sedtCompdescript,sedtHrname,sedtHrcontact;

    FButton btnSelect,btnUpload;

    Jobs newJob;

    RelativeLayout rootLayout;

    Categorymodel categorymodel;

    Uri saveUri;

    String datetime=Common.getDate(System.currentTimeMillis());

    ImageView imageback;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joblist);


        swipeRefreshLayout=findViewById(R.id.swipe_joblistlayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    if (getIntent() != null)
                        categoryId = getIntent().getStringExtra("CategoryId");
                    if (!categoryId.isEmpty())
                        loadJobList(categoryId);
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
                    if (getIntent() != null)
                        categoryId = getIntent().getStringExtra("CategoryId");
                    if (!categoryId.isEmpty())
                        loadJobList(categoryId);
                }
                else {
                    Snackbar.make(rootLayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });

        imageback=findViewById(R.id.logbackpressjoblist);
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rootLayout=findViewById(R.id.rootLayout);

        categorymodel=new Categorymodel();

        database=FirebaseDatabase.getInstance();
        jobList=database.getReference("Jobs");

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        recyclerView=findViewById(R.id.recycler_job);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fab=findViewById(R.id.joblistfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAddJobDialog();
            }
        });


    }



    private void showAddJobDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Joblist.this);
        alertDialog.setTitle("Add new Job");

        LayoutInflater inflater=this.getLayoutInflater();
        View add_job_layout=inflater.inflate(R.layout.add_new_job_layout,null);

        edtTitle=add_job_layout.findViewById(R.id.edttitle);
        edtCompname=add_job_layout.findViewById(R.id.edtcompname);
        edtExp=add_job_layout.findViewById(R.id.edtexp);
        edtLoc=add_job_layout.findViewById(R.id.edtloc);
        edtSkills=add_job_layout.findViewById(R.id.edtskills);
        edtVacancy=add_job_layout.findViewById(R.id.edtvacancy);
        edtWalkin=add_job_layout.findViewById(R.id.edtwalkin);
        edtSalary=add_job_layout.findViewById(R.id.edtsalary);
        edtJobdescript=add_job_layout.findViewById(R.id.edtjobdescript);
        edtIndustype=add_job_layout.findViewById(R.id.edtindustrytype);
        edtFuncarea=add_job_layout.findViewById(R.id.edtfuncarea);
        edtJobrole=add_job_layout.findViewById(R.id.edtjobrole);
        edtEmptype=add_job_layout.findViewById(R.id.edtemptype);
        edtDesiredcp=add_job_layout.findViewById(R.id.edtdesiredcp);
        edtCompweb=add_job_layout.findViewById(R.id.edtcompwebsite);
        edtCompdescript=add_job_layout.findViewById(R.id.edtcompdescript);
        edtHrname=add_job_layout.findViewById(R.id.edthrname);
        edtHrcontact=add_job_layout.findViewById(R.id.edthrcontact);


        btnSelect=add_job_layout.findViewById(R.id.btnSelect);
        btnUpload=add_job_layout.findViewById(R.id.btnUpload);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_job_layout);
        alertDialog.setIcon(R.drawable.job);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //here just create new category
                if (newJob!=null)
                {
                    jobList.push().setValue(newJob);
                    Snackbar.make(rootLayout,"New "+categorymodel.getName()+"job was added",Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void uploadImage() {
        if (saveUri !=null)
        {
            final ProgressDialog mDialog=new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Joblist.this,"Uploaded !!!",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newJob=new Jobs();
                                    newJob.setTitle(edtTitle.getText().toString());
                                    newJob.setCompanyName(edtCompname.getText().toString());
                                    newJob.setExperience(edtExp.getText().toString());
                                    newJob.setLocation(edtLoc.getText().toString());
                                    newJob.setSkills(edtSkills.getText().toString());
                                    newJob.setVacancies(edtVacancy.getText().toString());
                                    newJob.setWalkinTnV(edtWalkin.getText().toString());
                                    newJob.setSalary(edtSalary.getText().toString());
                                    newJob.setPostDate(datetime);
                                    newJob.setJobDescription(edtJobdescript.getText().toString());
                                    newJob.setIndustryType(edtIndustype.getText().toString());
                                    newJob.setFunctionalArea(edtFuncarea.getText().toString());
                                    newJob.setJobRole(edtJobrole.getText().toString());
                                    newJob.setEmploymentType(edtEmptype.getText().toString());
                                    newJob.setDesiredProfile(edtDesiredcp.getText().toString());
                                    newJob.setCompanyWebsite(edtCompweb.getText().toString());
                                    newJob.setCompanyDescription(edtCompdescript.getText().toString());
                                    newJob.setHrName(edtHrname.getText().toString());
                                    newJob.setHrContact(edtHrcontact.getText().toString());
                                    newJob.setCategoryId(categoryId);
                                    newJob.setCompanyImage(uri.toString());




                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Joblist.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+"%");
                        }
                    });
        }
    }

    private void chooseImage() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected");
        }
    }

    private void loadJobList(String categoryId) {

        Query jobListByCategoryId=jobList.orderByChild("categoryId").equalTo(categoryId);
        FirebaseRecyclerOptions<Jobs> options=new FirebaseRecyclerOptions.Builder<Jobs>()
                .setQuery(jobListByCategoryId,Jobs.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Jobs, JobViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JobViewHolder viewHolder, int position, @NonNull Jobs model) {
                viewHolder.job_title.setText(model.getTitle());
                Picasso.with(getBaseContext()).load(model.getCompanyImage())
                        .into(viewHolder.job_image);
                viewHolder.job_compname.setText(model.getCompanyName());
                viewHolder.job_exp.setText(model.getExperience());
                viewHolder.job_loc.setText(model.getLocation());
                viewHolder.job_skills.setText(model.getSkills());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @Override
            public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.job_item,parent,false);
                return new JobViewHolder(itemView);
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
        if(adapter != null) {
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