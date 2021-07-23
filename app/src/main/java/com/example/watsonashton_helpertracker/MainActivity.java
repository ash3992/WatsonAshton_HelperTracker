package com.example.watsonashton_helpertracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.watsonashton_helpertracker.fragments.LogInFragment;
import com.example.watsonashton_helpertracker.fragments.SignUpFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LogInFragment.LogInListener, SignUpFragment.SignUpListener {
private FirebaseDatabase database;
private DatabaseReference mDatabase;
private FirebaseAuth mAuth;
Spinner heightSpinner;
String message;
Context mContext;
    HashMap users = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
message = "account not created please fix fields";
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                LogInFragment.newInstance()).commit();

        //mAuth = FirebaseAuth.getInstance().getCurrentUser()
       // HashMap hashMap = new HashMap();
      /*  users.put("Id", 123);
        users.put("Email", "ashtonwatson1021@gmail.com");
        users.put("Password", "938974Aurhdd()");
        createNewUser("ashtonwatson1021ddddds1@gmail.com","aaaaaaaaaaaaa");*/
        //mDatabase.child("example").setValue(hashMap);


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

    @Override
    public void SignUpInfoEmpty() {
        Toast.makeText(this, "Please fill out all fields to continue.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void SignUpInfoNotEmpty(String fname, String lname, String email, String password, String uHeight, String uHair, String uEye, String weight) {
        users.put("First Name", fname);
        users.put("Last Name", lname);
        users.put("Email", email);
        users.put("Password",password);
        users.put("Height", uHeight);
        users.put("Hair", uHair);
        users.put("Eyes", uEye);
        users.put("Weight", weight);

      //  users.put("Password", password);
        createNewUser(email,password);
    }

    public void createNewUser(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull  Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("TAG", "success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            message = "New account created!";
                            updateUI(user);
                        }
                        if(!task.isSuccessful()){
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            if(e.getMessage().contains("The email address is badly formatted.")){
                                message = "The email address is badly formatted.";
                            }
                            else if(e.getMessage().contains("The email address is already in use by another account.")){
                                message = "The email address is already in use by another account.";

                            } else if(e.getMessage().contains("The given password is invalid. [ Password should be at least 6 characters ]")){
                                message = "The given password is invalid. [ Password should be at least 6 characters ]";
                            }

                            Log.e("LoginActivity", "Failed Registration", e);
                          //  message.hide();
                            return;
                        }

                    }
                });
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void updateUI(FirebaseUser user){
        String keyId = mDatabase.push().getKey();

        mDatabase.child("ashtonwatson1vvv021@gmailcom").setValue(users);
        user = mAuth.getCurrentUser();
        Log.d("TAG", "updateUI: "+mAuth.getCurrentUser().getEmail());

    }

}