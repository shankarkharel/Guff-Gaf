package com.example.guff_gaf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;

import static android.view.View.INVISIBLE;


public class RegistrationActivity extends AppCompatActivity {

    private TextInputLayout reg_displayname,reg_email,reg_password;
    private Button reg_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressBar reg_progressbar;
    public String name;
    //private MultiAutoCompleteTextView setting_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setTitle("Register here!");
       // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();


        //getting the instance of button and text input field from .xml.

        reg_displayname = (TextInputLayout)findViewById(R.id.reg_displayname);
        reg_email = (TextInputLayout)findViewById(R.id.reg_email);
       // reg_password = (TextInputLayout)findViewById(reg_password);
        reg_btn = (Button)findViewById(R.id.reg_btn);
        reg_password=(TextInputLayout)findViewById(R.id.reg_password);
       // setting_status=(MultiAutoCompleteTextView)findViewById(R.id.setting_status);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String Email= reg_email.getEditText().getText().toString();
               String Password = reg_password.getEditText().getText().toString();
               String Name = reg_displayname.getEditText().getText().toString();
               reg_progressbar = (ProgressBar)findViewById(R.id.reg_progressbar);
               //String Status = setting_status.getText().toString();

               //declaring a function with parameter and no return type to registetr a user.
                reg_progressbar.setVisibility(View.VISIBLE);
                userRegistration(Name,Email,Password);
            }
        });


    }

    private void userRegistration(final String Name, String Email, String Password) {

        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser Current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = Current_user.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(uid);
                    HashMap<String, String> usermap = new HashMap<>();
                    usermap.put("name", Name);
                    usermap.put("status", "Hii-I'm using guf-gaf.");
                    usermap.put("dp", "dp_link");
                    usermap.put("surname", "kharel");
                    usermap.put("defult_dp", "defult dp_link");
                    mDatabase.setValue(usermap);

                    reg_progressbar.setVisibility(INVISIBLE);


                    Intent reg_intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    reg_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(reg_intent);
                } else {
                    Toast.makeText(RegistrationActivity.this, "sorry!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(AuthResult authResult) {
                notification();
            }
        });
 name = Name;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notification(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(this,"n")
                .setContentText("Guff-gaf")
                .setSmallIcon(R.drawable.profile)
                .setAutoCancel(true)
                .setContentText("Welcome! "+ name +" Thanks for register");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(Integer.parseInt("999"),builder.build());
    }
}