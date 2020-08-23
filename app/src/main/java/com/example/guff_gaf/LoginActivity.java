package com.example.guff_gaf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout log_email,log_password;
    private Button log_btn;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Login here!");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        log_email = (TextInputLayout)findViewById(R.id.log_emai);
        log_password =(TextInputLayout)findViewById(R.id.log_password);
        log_btn =(Button)findViewById(R.id.log_btn);
        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = log_email.getEditText().getText().toString();
                String Password = log_password.getEditText().getText().toString();

                //declaring  a method with argument  and no return type to login  a registered user.
                        userLogin(Email,Password);

            }
        });
    }
    private void userLogin(String Email,String Password){
        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent log_intent = new Intent(LoginActivity.this,MainActivity.class);
                    log_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(log_intent);
                }else {
                    Toast.makeText(LoginActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}