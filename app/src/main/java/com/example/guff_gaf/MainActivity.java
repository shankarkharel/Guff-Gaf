package com.example.guff_gaf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
//Declaring an instance of firebase.
    private FirebaseAuth mAuth;

    private ViewPager mViewpager;
    private SectionAdapter mSectionAdapter;
    private TabLayout mtablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewpager = (ViewPager)findViewById(R.id.view_pager);
        mSectionAdapter = new SectionAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mSectionAdapter);
        mtablayout = (TabLayout)findViewById(R.id.main_tab);
        mtablayout.setupWithViewPager(mViewpager);
        //Initializing the firebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();


    }
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            Intent main_intent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(main_intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                Intent setting_intent = new Intent(MainActivity.this,SettingsActivity.class);
                //setting_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(setting_intent);
                break;
            case R.id.item1:
                Intent intent_item1 = new Intent(MainActivity.this,StartActivity.class);
                intent_item1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_item1);
                break;
            case R.id.item2:
                Intent intent_item2 = new Intent(MainActivity.this,RegistrationActivity.class);
                intent_item2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_item2);
                break;


            case R.id.all_user:
                Intent intent_item3 = new Intent(MainActivity.this,AllUsersActivity.class);
                //intent_item3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_item3);
                break;
           default:
               return true;
        }

    return true;}
}