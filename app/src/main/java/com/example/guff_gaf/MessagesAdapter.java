package com.example.guff_gaf;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private List<Messages> userMessagesList;

    public MessagesAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        CircleImageView chat_single_profile;
        TextView sender_single_text, receiver_singleText;
        //ImageView send_img;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            chat_single_profile = itemView.findViewById(R.id.chat_single_profile);
            sender_single_text = itemView.findViewById(R.id.receiver_single_text);
            //send_img = itemView.findViewById(R.id.send_img);
            receiver_singleText = itemView.findViewById(R.id.sender_single_text);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_single_layout, parent, false);
        mRef = FirebaseDatabase.getInstance().getReference().child("user");
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int i) {
        String currentUser = mAuth.getCurrentUser().getUid();
        Messages c = userMessagesList.get(i);




        String fromUserId = c.getFrom();
        Log.d("fromuserid", "onBindViewHolder: " + fromUserId);
        String fromMessageType = c.getType();
        Log.d("fromMessageType", "onBindViewHolder: " + fromMessageType);
        if (fromMessageType.equals("text")) {
            //holder.receiver_singleText.setFocusable(false);
            holder.receiver_singleText.setVisibility(View.INVISIBLE);
            holder.chat_single_profile.setVisibility(View.INVISIBLE);
            holder.sender_single_text.setEnabled(false);
            holder.receiver_singleText.setEnabled(false);
            holder.sender_single_text.setTextColor(Color.BLACK);
            //holder.send_img.setVisibility(View.INVISIBLE);
           // holder.send_img.setEnabled(false);
            // holder.send_img.setMaxWidth(30);

            if (fromUserId.equals(currentUser)) {
                // holder.sender_single_text.setFocusable(false);
                holder.receiver_singleText.setVisibility(View.VISIBLE);
                holder.sender_single_text.setVisibility(View.INVISIBLE);
                holder.receiver_singleText.setTextColor(Color.BLACK);
                holder.sender_single_text.setEnabled(false);
                //holder.send_img.setVisibility(View.INVISIBLE);
                holder.receiver_singleText.setEnabled(false);
              //  holder.send_img.setEnabled(false);
                holder.receiver_singleText.setText(" " + c.getMsg() + " ");
                String lastMsg = c.getMsg().toLowerCase();


            } else {
                holder.sender_single_text.setVisibility(View.INVISIBLE);
                holder.sender_single_text.setVisibility(View.VISIBLE);
                holder.chat_single_profile.setVisibility(View.VISIBLE);
                holder.sender_single_text.setEnabled(false);
                holder.receiver_singleText.setEnabled(false);
                //holder.send_img.setVisibility(View.INVISIBLE);
              //  holder.send_img.setEnabled(false)
                //lastMsg = c.getMsg().toLowerCase();
                holder.sender_single_text.setText(" " + c.getMsg() + " ");
                //loading profile of sender
                mRef.child(fromUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("dp")) {
                            String Url = snapshot.child("dp").getValue().toString();
                            Picasso.get().load(Url).
                                    placeholder(R.drawable.profile).fit().into(holder.chat_single_profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } /*else if (fromMessageType.equals("image")) {
            holder.receiver_singleText.setVisibility(View.INVISIBLE);
            holder.chat_single_profile.setVisibility(View.INVISIBLE);
            holder.sender_single_text.setVisibility(View.INVISIBLE);
            holder.sender_single_text.setEnabled(false);
            holder.receiver_singleText.setEnabled(false);
            holder.sender_single_text.setTextColor(Color.BLACK);
            holder.send_img.setVisibility(View.INVISIBLE);
            holder.send_img.setEnabled(false);
            // holder.send_img.setMaxWidth(30);
            if (fromUserId.equals(currentUser)) {
                Log.d("Url", "onBindViewHolder: image url" + c.getMsg());
                holder.sender_single_text.setVisibility(View.INVISIBLE);
                holder.sender_single_text.setVisibility(View.INVISIBLE);
                holder.chat_single_profile.setVisibility(View.INVISIBLE);
                holder.receiver_singleText.setVisibility(View.INVISIBLE);
                holder.sender_single_text.setEnabled(false);
                holder.receiver_singleText.setEnabled(false);
                Picasso.get().load(c.getMsg()).placeholder(R.drawable.placeholder).into(holder.chat_single_profile);

            } else {
                holder.receiver_singleText.setVisibility(View.INVISIBLE);
                holder.sender_single_text.setVisibility(View.INVISIBLE);
                holder.chat_single_profile.setVisibility(View.VISIBLE);
                holder.send_img.setVisibility(View.VISIBLE);
                Picasso.get().load(c.getMsg()).placeholder(R.drawable.placeholder).into(holder.send_img);
            }

        }*/

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
