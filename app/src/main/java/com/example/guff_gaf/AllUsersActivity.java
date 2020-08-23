package com.example.guff_gaf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private RecyclerView user_list;
    private CircleImageView user_single_profile;
    private Toolbar user_toolbar;
    private FirebaseDatabase muserdatabase;
    private FirebaseRecyclerOptions<Users> options;
    private  FirebaseRecyclerAdapter<Users, MyViewHolder> adapter;
    private DatabaseReference ref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        user_list = (RecyclerView)findViewById(R.id.user_list);
        user_list.setHasFixedSize(true);
        user_list.setLayoutManager(new  LinearLayoutManager(this));
        ref = FirebaseDatabase.getInstance().getReference().child("user");
        getSupportActionBar().setTitle("All users");
        user_single_profile = (CircleImageView)findViewById(R.id.user_single_profile);
        options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(ref,Users.class).build();
        adapter= new FirebaseRecyclerAdapter<Users,MyViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Users model) {
                holder.online_state.setVisibility(View.INVISIBLE);
                holder.online_state.setEnabled(false);
                holder.user_single_name.setText(model.getName());
                holder.user_single_status.setText(model.getStatus());
                String url = model.getDp();
                //holder.user_single_profile.setImageURI(Uri.parse(url));
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.profile)
                        .fit()
                        .into(holder.user_single_profile);
                final String user_key = getRef(position).getKey();
                holder.user_single_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AllUsersActivity.this,ProfileActivity.class);
                        intent.putExtra("user_key",user_key);
                        startActivity(intent);
                    }
                });

            }

           @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

               View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout,parent,false);

               return new MyViewHolder(v);


            }
        };
        adapter.startListening();
        user_list.setAdapter(adapter);



    }
}
