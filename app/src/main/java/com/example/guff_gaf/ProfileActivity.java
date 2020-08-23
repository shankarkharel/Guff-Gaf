package com.example.guff_gaf;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Attributes;

public class ProfileActivity extends AppCompatActivity {
private Button profile_status,profile_displayname,profile_sendrequest,profile_declinerequest;
private DatabaseReference mref;
private DatabaseReference requestData;
private DatabaseReference friendsData;
private ImageView profile_image;
private FirebaseUser mcurrentuser;
private String request_state;
private ImageButton prifile_back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String key = getIntent().getStringExtra("user_key");
        profile_displayname = findViewById(R.id.profile_displayname);
        prifile_back_button = (ImageButton)findViewById(R.id.prifile_back_button);
        profile_displayname.setText(key);
        profile_status = findViewById(R.id.profile_status);
        profile_sendrequest = findViewById(R.id.profile_sendrequest);
        profile_declinerequest = findViewById(R.id.profile_declinerequest);
        profile_declinerequest.setVisibility(View.INVISIBLE);
        profile_declinerequest.setEnabled(false);
        profile_image = findViewById(R.id.profile_imageView);
        mref = FirebaseDatabase.getInstance().getReference().child("user").child(key);
        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        requestData = FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendsData = FirebaseDatabase.getInstance().getReference().child("friends");
        request_state = "not_friends";
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String Name = snapshot.child("name").getValue().toString();
                String Status = snapshot.child("status").getValue().toString();
                String url = snapshot.child("dp").getValue().toString();
                Picasso.get()
                        .load(url)
                        .centerCrop()
                        .fit()
                        .into(profile_image);


                // Glide.with(ProfileActivity.this).load(url).into(profile_image);
                profile_displayname.setText(Name);
                profile_status.setText(Status);
                requestData.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(key)){
                            String request_type = snapshot.child(key).child("request_type").getValue().toString();
                            //String name = snapshot.child(mcurrentuser.getUid()).child("name").getValue().toString();
                            if (request_type.equals("Received")){
                                request_state = "request_Received";
                                profile_sendrequest.setText("Accept Request");
                                profile_declinerequest.setEnabled(true);
                                profile_declinerequest.setVisibility(View.VISIBLE);
                                notification("You have a new Friend Request");

                            }else if (request_type.equals("sent")){
                                request_state = "Request sent";
                                profile_sendrequest.setText("Cancel Request");
                                profile_declinerequest.setEnabled(false);
                                profile_declinerequest.setVisibility(View.INVISIBLE);


                            }
                        }else {
                            friendsData.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                              if (snapshot.hasChild(key)){
                                  request_state ="Request Accepted";
                                  profile_sendrequest.setText("Unfriend "+Name);
                                  profile_declinerequest.setEnabled(false);
                                  profile_declinerequest.setVisibility(View.INVISIBLE);
                              }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        profile_sendrequest.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                profile_sendrequest.setEnabled(false);


                //==================Not friends State =============


                if (request_state.equals("not_friends")) {
                    profile_declinerequest.setEnabled(false);
                    profile_declinerequest.setVisibility(View.INVISIBLE);
                    requestData.child(mcurrentuser.getUid()).child(key).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                requestData.child(key).child(mcurrentuser.getUid()).child("request_type").setValue("Received")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                notification("You have a new Friend Request !");

                                            }
                                        });
                            }else{            //key is the id of the user whose profile we are viewing.
                                              //request is received by  the user whose profile we are in
                                Toast.makeText(ProfileActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            profile_sendrequest.setEnabled(true);
                            request_state = "Request sent";

                            profile_sendrequest.setText("Cancel Request");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                //=========cancel request====================
if (request_state.equals("Request sent")){
    profile_declinerequest.setEnabled(false);
    profile_declinerequest.setVisibility(View.INVISIBLE);

    requestData.child(mcurrentuser.getUid()).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            requestData.child(key).child(mcurrentuser.getUid()).removeValue();
        }
    }).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            profile_sendrequest.setEnabled(true);
            request_state = "not_friends";
            profile_sendrequest.setText("Send Request");
        }
    });
}
               //===========Request Accepting ===========
if (request_state.equals("request_Received")){
    notification("You have a new Friend Request !");

    profile_declinerequest.setEnabled(false);
    profile_declinerequest.setVisibility(View.INVISIBLE);

    final String current_date = DateFormat.getDateInstance().format(new Date());
    friendsData.child(mcurrentuser.getUid()).child(key)
            .child("date").setValue(current_date)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            friendsData.child(key).child(mcurrentuser.getUid()).child("date").setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    notification("Your request was accepted!");
                }
            });
          requestData.child(key).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                  requestData.child(mcurrentuser.getUid()).child(key).removeValue();
                  profile_sendrequest.setEnabled(true);
                  request_state = "friends";

                  mref.addListenerForSingleValueEvent(new ValueEventListener() {
                      @RequiresApi(api = Build.VERSION_CODES.O)
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                         String Name = snapshot.child("name").getValue().toString();
                          profile_sendrequest.setText("Unfriend "+ Name);
                          notification("You have a new Friend its "+ Name);
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {
                          Toast.makeText(ProfileActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                      }
                  });


              }
          });
        }
    });

}
            }
        });

        prifile_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile_intent= new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(profile_intent);
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notification(String request_state){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Notification.Builder builder = new Notification.Builder(this,"n")
                .setContentText("Guff-gaf")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setAutoCancel(true)
                .setContentText(request_state);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(Integer.parseInt("999"),builder.build());




    }

}