package com.example.watsonashton_helpertracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.watsonashton_helpertracker.fragments.LogInFragment;
import com.example.watsonashton_helpertracker.fragments.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements LogInFragment.LogInListener, SignUpFragment.SignUpListener {
private FirebaseDatabase database;
private DatabaseReference mDatabase;
private FirebaseAuth mAuth;
Spinner heightSpinner;
Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
       // heightSpinner = findViewById(R.id.spinnerHeight);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                LogInFragment.newInstance()).commit();

        //mAuth = FirebaseAuth.getInstance().getCurrentUser()

    }

    @Override
    public void LogInNewUserClicked() {
        Toast.makeText(this, "Sign Up", Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                SignUpFragment.newInstance()).commit();

    }


    @Override
    public void SignUpAlreadyAnUserClicked() {
        Toast.makeText(this, "Log In", Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                LogInFragment.newInstance()).commit();
    }


}