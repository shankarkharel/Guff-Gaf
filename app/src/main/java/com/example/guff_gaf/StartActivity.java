package com.example.guff_gaf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button start_btn1,start_btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    start_btn1 = (Button)findViewById(R.id.start_btn1);
    start_btn2 = (Button)findViewById(R.id.start_btn2);


    start_btn1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent start_intent1= new Intent(StartActivity.this,RegistrationActivity.class);
            startActivity(start_intent1);
            finish();
        }
    });


start_btn2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent start_intent2 = new Intent(StartActivity.this,LoginActivity.class);
        startActivity(start_intent2);
    finish();
    }
});

    }


}