 package com.example.guff_gaf;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


 public class ChatFragment extends Fragment {
     private View mview;
     private RecyclerView chat_list;
     private CircleImageView user_single_profile;
     private Toolbar user_toolbar;
     private FirebaseDatabase muserdatabase;
     private FirebaseRecyclerOptions<Chat> options;
     private FirebaseRecyclerAdapter<Chat, MyViewHolder> adapter;
     private DatabaseReference ref;
     private FirebaseAuth mAuth;
     private DatabaseReference chatRef;
     String online_user_id;
     private DatabaseReference mRootRef;




     public ChatFragment() {
         // Required empty public constructor
     }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_chat, container, false);
        chat_list = (RecyclerView)mview.findViewById(R.id.chat_list);
       chat_list.setHasFixedSize(true);
        chat_list.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance() ;
        if(mAuth.getCurrentUser() !=null){
            online_user_id = mAuth.getCurrentUser().getUid();}
        mRootRef=FirebaseDatabase.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference().child("chat").child(online_user_id);
        final DatabaseReference Friends_ref = FirebaseDatabase.getInstance().getReference().child("user");
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("messages").child(online_user_id);
        options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(ref,Chat.class).build();
        adapter= new FirebaseRecyclerAdapter<Chat, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, int i, @NonNull Chat model) {
                // holder.user_single_status.setText("Since: " + model.getDate());
                final String Friends_id = getRef(i).getKey();
                Friends_ref.child(Friends_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String name = snapshot.child("name").getValue().toString();
                        String Url = (String) snapshot.child("dp").getValue();
                        holder.user_single_name.setText(name);
                        // holder.user_single_status.setText(MessagesAdapter.lastMsg);
                        Picasso.get().load(Url).placeholder(R.drawable.profile).into(holder.user_single_profile);
                        String online = snapshot.child("online").getValue().toString();
                        if (online.equals("true")) {
                            holder.online_state.setVisibility(View.VISIBLE);

                        } else {
                            holder.online_state.setVisibility(View.INVISIBLE);

                        }
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                chatIntent.putExtra("user_key",Friends_id);
                                startActivity(chatIntent);
                            }
                        });

                        Query chatQuery = chatRef.child(Friends_id);
                                chatQuery.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                String msg =  snapshot.child("msg").getValue().toString();
                                holder.user_single_status.setText(msg);
                            }


                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("error", "onCancelled: database error" + error);
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
                chat_list.setAdapter(adapter);
                return mview;
    }
}