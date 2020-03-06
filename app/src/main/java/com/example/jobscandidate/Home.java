package com.example.jobscandidate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobscandidate.Common.Common;
import com.example.jobscandidate.Interface.ItemClickListener;
import com.example.jobscandidate.Model.Candidate;
import com.example.jobscandidate.Model.Categorymodel;
import com.example.jobscandidate.Model.Token;
import com.example.jobscandidate.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database,resumedatabase;
    DatabaseReference category,resumedbrefrence;

    FirebaseStorage storage;
    StorageReference storageReference;

    RecyclerView catrecycler;
    RecyclerView.LayoutManager layoutManager;

    TextView txtFullName,resumetxt,profilename,profileemail;
    ImageView imageView;
    FirebaseRecyclerAdapter<Categorymodel,CategoryViewHolder> adapter;

    FButton resumeselect,resumeupload,profileselect,profileupload;

    Uri saveFileUri,saveImageUri;
    private final int PICK_FILE_REQUEST=22;
    private final int PICK_IMAGE_REQUEST=33;

    SwipeRefreshLayout swipeRefreshLayout;

    CoordinatorLayout rootlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences preferences=getSharedPreferences("prefs",MODE_PRIVATE);
        boolean firstStart=preferences.getBoolean("firstStart",true);

        if (firstStart){
        showTipsDialog();
        }

        rootlayout=findViewById(R.id.homerootlayout);

        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
                );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    loadCategory();
                }
                else {
                    Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });

        //Default load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    loadCategory();
                }
                else {
                    Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });

        storage=FirebaseStorage.getInstance();
        resumedatabase=FirebaseDatabase.getInstance();
        resumedbrefrence=resumedatabase.getReference("Candidate");
        storageReference=storage.getReference();

        Paper.init(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,SearchActivity.class));
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.menu);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerVisible(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set name and image of candidate
        View headerView=navigationView.getHeaderView(0);
        txtFullName=headerView.findViewById(R.id.txtFullName);
        imageView=headerView.findViewById(R.id.imageView);
        try {
            txtFullName.setText(Common.currentCandidate.getName());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            Picasso.with(getBaseContext()).load(Common.currentCandidate.getImage())
                    .into(imageView);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        //Init Firebase
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");

        //Load category
        catrecycler=findViewById(R.id.catrecyclerview);
        catrecycler.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        catrecycler.setLayoutManager(new GridLayoutManager(this,2));

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void showTipsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("There is one tip for you!!")
                .setMessage("Please first update your profile and upload your resume..")
                .setIcon(R.drawable.hi)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();

        SharedPreferences preferences=getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("firstStart",false);
        editor.apply();
    }

    private void updateToken(String token) {
        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,false);
        try {
            tokens.child(Common.currentCandidate.getPhone()).setValue(data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadCategory() {

        FirebaseRecyclerOptions<Categorymodel> options=new FirebaseRecyclerOptions.Builder<Categorymodel>()
                .setQuery(category,Categorymodel.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Categorymodel, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder viewHolder, int position, @NonNull Categorymodel model) {

                viewHolder.txtCategoryName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.catImage);
                final Categorymodel clickItem=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get category Id and send to new activity
                        Intent jobList=new Intent(Home.this,Joblist.class);
                        //Because categoryId is key, so we just getkey of this item
                        jobList.putExtra("categoryId",adapter.getRef(position).getKey());
                        startActivity(jobList);
                    }
                });
            }

            @Override
            public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
                return new CategoryViewHolder(itemView);
            }
        };
        adapter.startListening();
        catrecycler.setAdapter(adapter);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_updateprofile) {
            showProfileDialog();
        } else if (id == R.id.nav_savedjob) {
            startActivity(new Intent(Home.this,SavedjobActivity.class));

        } else if(id == R.id.nav_updateresume){
            showResumeDialog();

        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout){
            //delete remember user and pass
            Paper.book().destroy();
            //logout
            Intent logout =new Intent(Home.this,MainActivity.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showProfileDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add your Profile");

        LayoutInflater inflater=this.getLayoutInflater();
        View add_profile_layout=inflater.inflate(R.layout.update_profile,null);

        profilename=add_profile_layout.findViewById(R.id.profile_name);
        profileemail=add_profile_layout.findViewById(R.id.profile_email);
        profileselect=add_profile_layout.findViewById(R.id.profilebtnSelect);
        profileupload=add_profile_layout.findViewById(R.id.profilebtnUpload);

        profileselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        profileupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        alertDialog.setView(add_profile_layout);
        alertDialog.setIcon(R.drawable.blackuser);
        alertDialog.show();


    }

    private void uploadImage() {
        if (saveImageUri != null)
        {
            final ProgressDialog mDialog=new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName=UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("profileimage/"+imageName);
            imageFolder.putFile(saveImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String,Object> updateimage=new HashMap<>();
                                    updateimage.put("image",uri.toString());
                                    FirebaseDatabase.getInstance()
                                            .getReference("Candidate")
                                            .child(Common.currentCandidate.getPhone())
                                            .updateChildren(updateimage);

                                    Map<String,Object> updatename=new HashMap<>();
                                    updatename.put("name",profilename.getText().toString());
                                    FirebaseDatabase.getInstance()
                                            .getReference("Candidate")
                                            .child(Common.currentCandidate.getPhone())
                                            .updateChildren(updatename);

                                    Map<String,Object> updatemail=new HashMap<>();
                                    updatemail.put("email",profileemail.getText().toString());
                                    FirebaseDatabase.getInstance()
                                            .getReference("Candidate")
                                            .child(Common.currentCandidate.getPhone())
                                            .updateChildren(updatemail)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mDialog.dismiss();
                                                    Toast.makeText(Home.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded "+progress+"%");
                }
            });
        }
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image"),PICK_IMAGE_REQUEST);
    }
    private void uploadFile() {
        if (saveFileUri != null)
        {
            final ProgressDialog mDialog=new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String fileName= UUID.randomUUID().toString();
            final StorageReference fileFolder=storageReference.child("resume/"+fileName);
            fileFolder.putFile(saveFileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String,Object> updateresume=new HashMap<>();
                                    updateresume.put("resume",uri.toString());
                                    FirebaseDatabase.getInstance()
                                            .getReference("Candidate")
                                            .child(Common.currentCandidate.getPhone())
                                            .updateChildren(updateresume)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mDialog.dismiss();
                                                    Toast.makeText(Home.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+"%");
                        }
                    });

        }
    }

    private void showResumeDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add your Resume");

        LayoutInflater inflater=this.getLayoutInflater();
        View add_resume_layout=inflater.inflate(R.layout.update_resume,null);

        resumetxt=add_resume_layout.findViewById(R.id.resumetv);
        resumeselect=add_resume_layout.findViewById(R.id.resumebtnSelect);
        resumeupload=add_resume_layout.findViewById(R.id.resumebtnUpload);

        resumeselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        resumeupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

        alertDialog.setView(add_resume_layout);
        alertDialog.setIcon(R.drawable.resume);
        alertDialog.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            saveFileUri=data.getData();
            resumetxt.setText("A File is Selected : "+ data.getData().getLastPathSegment());
            resumeselect.setText("File Selected!");

        }
        else if (requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            saveImageUri=data.getData();
            profileselect.setText("Image Selected!");
        }
    }

    private void chooseFile() {
        Intent intent=new Intent();
        intent.setType("docx/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Docx"),PICK_FILE_REQUEST);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}