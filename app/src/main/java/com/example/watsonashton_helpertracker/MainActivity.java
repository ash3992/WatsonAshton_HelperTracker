package com.example.watsonashton_helpertracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.watsonashton_helpertracker.fragments.LogInFragment;

public class MainActivity extends AppCompatActivity implements LogInFragment.LogInListener, SignUpFragment.SignUpListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                LogInFragment.newInstance()).commit();
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