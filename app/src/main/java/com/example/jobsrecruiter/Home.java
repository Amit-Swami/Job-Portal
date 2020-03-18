package com.example.jobsrecruiter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.example.jobsrecruiter.Common.Common;
import com.example.jobsrecruiter.Interface.ItemClickListener;
import com.example.jobsrecruiter.Model.Categorymodel;
import com.example.jobsrecruiter.Model.Token;
import com.example.jobsrecruiter.ViewHolder.CategoryViewHolder;
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

    FirebaseDatabase database;
    DatabaseReference categories;
    FirebaseRecyclerAdapter<Categorymodel, CategoryViewHolder> adapter;

    FirebaseStorage storage;
    StorageReference storageReference;

    //View
    RecyclerView recycler_cat;
    RecyclerView.LayoutManager layoutManager;

    TextView profilename,profileemail,recruitername;
    ImageView recruiterimage;
    FButton profileselect,profileupload;

    private final int PICK_IMAGE_REQUEST=44;
    Uri saveImageUri;

    CoordinatorLayout rootlayout;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadCategory();
                }
                else
                {
                    Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    loadCategory();
                }
                else
                {
                    Snackbar.make(rootlayout,"Please check your connection !!",Snackbar.LENGTH_LONG).show();
                    return;

                }

            }
        });
        rootlayout=findViewById(R.id.homerootlayout);

        Paper.init(this);

        database=FirebaseDatabase.getInstance();
        categories=database.getReference("Category");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();


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
        recruitername=headerView.findViewById(R.id.txtFullName);
        try {
            recruitername.setText(Common.currentRecruiter.getName());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        recruiterimage=headerView.findViewById(R.id.imageView);
        try {
            Picasso.with(getBaseContext()).load(Common.currentRecruiter.getImage())
                    .into(recruiterimage);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        recycler_cat=findViewById(R.id.catrecyclerview);
        recycler_cat.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_cat.setLayoutManager(new GridLayoutManager(this,2));

        //Send Token
        updateToken(FirebaseInstanceId.getInstance().getToken());


    }
    private void updateToken(String token) {
        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,true);
        try {
            tokens.child(Common.currentRecruiter.getPhone()).setValue(data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadCategory() {

        FirebaseRecyclerOptions<Categorymodel> options=new FirebaseRecyclerOptions.Builder<Categorymodel>()
                .setQuery(categories,Categorymodel.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Categorymodel, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder viewHolder, int position, @NonNull Categorymodel model) {
                viewHolder.txtCategoryName.setText(model.getName());
                Picasso.with(Home.this).load(model.getImage())
                        .into(viewHolder.catImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent jobList=new Intent(Home.this,Joblist.class);
                        jobList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(jobList);

                    }
                });
            }

            @Override
            public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_item,parent,false);
                return new CategoryViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();//refresh data if have data changed
        recycler_cat.setAdapter(adapter);
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

        if (id == R.id.nav_applycandidate) {
            startActivity(new Intent(Home.this,Appliedcandidate.class));
        }else if (id == R.id.nav_updateprofile) {
            showProfileDialog();
        }
        else if (id == R.id.nav_logout) {
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

            String imageName= UUID.randomUUID().toString();
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
                                            .child(Common.currentRecruiter.getPhone())
                                            .updateChildren(updateimage);

                                    Map<String,Object> updatename=new HashMap<>();
                                    updatename.put("name",profilename.getText().toString());
                                    FirebaseDatabase.getInstance()
                                            .getReference("Candidate")
                                            .child(Common.currentRecruiter.getPhone())
                                            .updateChildren(updatename);

                                    Map<String,Object> updatemail=new HashMap<>();
                                    updatemail.put("email",profileemail.getText().toString());
                                    FirebaseDatabase.getInstance()
                                            .getReference("Candidate")
                                            .child(Common.currentRecruiter.getPhone())
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

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}