package com.example.guff_gaf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private TextInputLayout status_here;
    private Button save_changes;
    private DatabaseReference statusdatabase;
    private ProgressDialog mprogress;
    private FirebaseUser current_user;
   // private String current_uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mprogress = new ProgressDialog(this);
        getSupportActionBar().setTitle("Status setting");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        status_here = (TextInputLayout)findViewById(R.id.status_here);
        save_changes =(Button)findViewById(R.id.save_statusbtn);
        current_user = FirebaseAuth.getInstance().getCurrentUser();
         String current_uid = current_user.getUid();
        final String status_value = getIntent().getStringExtra("status");
        statusdatabase = FirebaseDatabase.getInstance().getReference().child("user").child(current_uid);

//showing the same status on both setting activity and status activity.

        status_here.getEditText().setText(status_value);
        save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress
                mprogress.setTitle("Saving changes!");
                mprogress.setMessage("please wait while we save changes");
                mprogress.show();

                String status = status_here.getEditText().getText().toString();
                statusdatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()) {
                           mprogress.dismiss();
                       }else {
                           Toast.makeText(StatusActivity.this,"changesm were not saved",Toast.LENGTH_LONG).show();
                       }
                    }
                });
            }
        });

    }
}