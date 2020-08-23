package com.example.guff_gaf;

import android.app.VoiceInteractor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.load.Option;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class RequestFragment extends Fragment {
    private View mview;
    private FirebaseRecyclerOptions<Friend_Request>options;
    private FirebaseRecyclerAdapter<Friend_Request,MyViewHolder>adapter;
    private RecyclerView request_list;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private String currentUser;



    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mview = inflater.inflate(R.layout.fragment_request, container, false);
        request_list = mview.findViewById(R.id.request_list);
        request_list.setHasFixedSize(true);
        request_list.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance() ;
        if (mAuth.getCurrentUser()!=null){
        currentUser = mAuth.getCurrentUser().getUid();}
       // Log.d("user", "onCreateView: "+currentUser);
        if (currentUser!=null) {

            ref = FirebaseDatabase.getInstance().getReference().child("friend_request").child(currentUser);
            final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user");
            options = new FirebaseRecyclerOptions.Builder<Friend_Request>().setQuery(ref, Friend_Request.class).build();
            adapter = new FirebaseRecyclerAdapter<Friend_Request, MyViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final MyViewHolder holder, int i, @NonNull Friend_Request model) {
                    holder.online_state.setEnabled(false);
                    holder.online_state.setVisibility(View.INVISIBLE);
                    holder.user_single_status.setText(model.getRequest_type());
                    String list_user_id = getRef(i).getKey();
                    userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child("name").getValue().toString();
                            String Url = snapshot.child("dp").getValue().toString();
                            holder.user_single_name.setText(name);
                            Picasso.get()
                                    .load(Url)
                                    .placeholder(R.drawable.profile)
                                    .into(holder.user_single_profile);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @NonNull
                @Override
                public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout, parent, false);

                    return new MyViewHolder(v);
                }
            };
            adapter.startListening();
            request_list.setAdapter(adapter);
            return mview;

        }else {
            return mview;
        }       }
    }
