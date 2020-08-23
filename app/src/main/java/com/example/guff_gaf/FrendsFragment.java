package com.example.guff_gaf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class FrendsFragment extends Fragment {

    private View mview;
    private RecyclerView friends_list;
    private CircleImageView user_single_profile;
    private Toolbar user_toolbar;
    private FirebaseDatabase muserdatabase;
    private FirebaseRecyclerOptions<Friends> options;
    private FirebaseRecyclerAdapter<Friends, MyViewHolder> adapter;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private  DatabaseReference chatRef;
    String online_user_id;




    public FrendsFragment() {
        // Required empty public constructor
        }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_frends, container, false);

        friends_list = (RecyclerView)mview.findViewById(R.id.friends_list);
        friends_list.setHasFixedSize(true);
        friends_list.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance() ;
        if(mAuth.getCurrentUser() !=null){
        online_user_id = mAuth.getCurrentUser().getUid();}
        ref = FirebaseDatabase.getInstance().getReference().child("friends").child(online_user_id);
        final DatabaseReference Friends_ref = FirebaseDatabase.getInstance().getReference().child("user");
         final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("messages").child(online_user_id);
        // ref = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_uid);
        //user_single_profile = (CircleImageView)findViewById(R.id.user_single_profile);


        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(ref,Friends.class).build();
        adapter= new FirebaseRecyclerAdapter<Friends, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout,parent,false);
                return new MyViewHolder(v);

            }


            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, int i, @NonNull Friends model) {
                holder.user_single_status.setText("Since: "+ model.getDate());
                final String Friends_id = getRef(i).getKey();

                Friends_ref.child(Friends_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String name = snapshot.child("name").getValue().toString();
                        String Url = (String) snapshot.child("dp").getValue();
                        holder.user_single_name.setText(name);
                        Picasso.get().load(Url).placeholder(R.drawable.profile).into(holder.user_single_profile);
                        String online =  snapshot.child("online").getValue().toString();
                        if (online.equals("true")){
                            holder.online_state.setVisibility(View.VISIBLE);

                        }else {
                            holder.online_state.setVisibility(View.INVISIBLE);

                        }
                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] =new CharSequence[]{"SEND MESSAGES","VIEW PROFILE"};
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("YOU WANT TO?");
                                builder.setIcon(R.drawable.ic_alert);
                                builder.setItems(options , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which== 0){
                                             Intent intent0 = new Intent(getContext(),ChatActivity.class);
                                            intent0.putExtra("user_key",Friends_id);
                                            Query chatQuery = chatRef.child(Friends_id);

                                           //DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                           // DatabaseReference message_push = rootRef.child("messages").child(online_user_id).child(Friends_id).push();
                                            //String push_id = message_push.getKey();
                                            startActivity(intent0);
                                        }

                                        if (which==1){
                                            Intent intent1 = new Intent(getContext(),ProfileActivity.class);
                                            intent1.putExtra("user_key",Friends_id);
                                            startActivity(intent1);
                                        }

                                    }
                                });
                                builder.show();
                                }

                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        };
        adapter.startListening();
        friends_list.setAdapter(adapter);
        return mview;

    }

}