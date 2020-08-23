package com.example.guff_gaf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.net.HttpCookie;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    //layout.
   private CircleImageView profile_image;
   private MultiAutoCompleteTextView setting_status;
   private EditText setting_displayname;
   private Button setting_statusbtn,setting_profilebtn;
   private DatabaseReference muserdatabase;
   private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getSupportActionBar().setTitle("Profile Setting");
        setContentView(R.layout.activity_settings);
        profile_image = (CircleImageView)findViewById(R.id.profile_image);
        setting_displayname = (EditText)findViewById(R.id.setting_displayname);
        setting_status = (MultiAutoCompleteTextView)findViewById(R.id.setting_status);
        setting_statusbtn = (Button)findViewById(R.id.setting_statusbtn);
        setting_profilebtn = (Button)findViewById(R.id.setting_profilebtn);
        setting_status.setEnabled(false);
        setting_displayname.setEnabled(false);

        String current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        muserdatabase = FirebaseDatabase.getInstance().getReference().child("user").child(current_uid);
        final StorageReference profileref = storageReference.child("profile_images/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
        muserdatabase.keepSynced(true);

        profileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               // Glide.with(SettingsActivity.this).load(uri).into(profile_image);
                Picasso.get().load(uri).
                        placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image);

            }
        });

        ///for firebase cloud storage.
       // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        muserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String defualt_dp = snapshot.child("defult_dp").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
               String dp = snapshot.child("dp").getValue().toString();
                setting_statusbtn.setEnabled(true);
                //setting_displayname.setEnabled(true);
                setting_displayname.setText(name);
                setting_status.setText(status);
                Picasso.get().load(dp).
                        placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        setting_statusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //declaring and getting refrence of string value of setting_status(MultilineEditText) to show the same status on Statussetting Activity.
                String status_value = setting_status.getText().toString();
                muserdatabase.child("status").setValue(status_value);
                Intent setting_intent = new Intent(SettingsActivity.this,StatusActivity.class);
                //for getting same status displayed on both setting activity and status activity
                 setting_intent.putExtra("status",status_value);
                startActivity(setting_intent);
            }
        });
        setting_profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallary_intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallary_intent,1000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if(resultCode==Activity.RESULT_OK){
                Uri uri = data.getData();
               // profile_image.setImageURI(uri);

                uploadToFirebase(uri);
            }
        }}

    private void uploadToFirebase(Uri uri) {
        final StorageReference fileref = storageReference.child("profile_images/"+FirebaseAuth.getInstance()
                .getCurrentUser().getUid()+".jpg");
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                if (taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String Url = uri.toString();
                            //createNewPost(imageUrl);
                            muserdatabase.child("dp").setValue(Url);
                            Log.d("url", "onSuccess: url"+Url);

                        }
                    });
                }

                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(SettingsActivity.this).load(uri)
                            .placeholder(R.drawable.profile).into(profile_image);
                }
            });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, e+".", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SettingsActivity.this, "Uploading...", Toast.LENGTH_LONG).show();
            }
        });
       /* fileref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
        if(task.isSuccessful()){

            muserdatabase.child("Dp").setValue(url);
        }
    }
});*/

    }
}
