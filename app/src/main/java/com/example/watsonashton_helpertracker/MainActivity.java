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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LogInFragment.LogInListener, SignUpFragment.SignUpListener {
private FirebaseDatabase database;
private DatabaseReference mDatabase;
private FirebaseAuth mAuth;
String message;
Context mContext;
String userEmail;
String TAG = "TAG";
Boolean userFromLogin;
Boolean userFromNewAccount;
HashMap<String, String> users = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                LogInFragment.newInstance()).commit();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String name = user.getEmail();
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        }else{

        }
    }

    @Override
    public void LogInNewUserClicked() {
        Toast.makeText(this, "Sign Up", Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                SignUpFragment.newInstance()).commit();

    }



    @Override
    public void LogInFieldsEmpty() {
        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void LogInUser(String email, String password) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("TAG", "success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    //  message = "New account created!";
                    userEmail = email;
                    userFromLogin = true;
                    userFromNewAccount = false;
                    updateUI(user);

                }else{
                    try
                    {
                        throw task.getException();
                    }
                    // if user enters wrong email.
                    catch (FirebaseAuthInvalidUserException invalidEmail)
                    {
                        Log.d(TAG, "onComplete: invalid_email");


                        // TODO: take your actions!
                        message = "Invalid email entered!";
                    }
                    // if user enters wrong password.
                    catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                    {
                        Log.d(TAG, "onComplete: wrong_password");
                        message = "Invalid password entered!";

                        // TODO: Take your action
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "onComplete: " + e.getMessage());
                    }
                }
            }
        });
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                          //  message = "New account created!";
                            userEmail = email;
                            userFromNewAccount = true;
                            userFromLogin = false;
                            updateUI(user);

                        }
                        if(!task.isSuccessful()){
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            if(e.getMessage().contains("The email address is badly formatted.")){
                                message = "The email address is badly formatted.";
                                messageToUser();
                            }
                            else if(e.getMessage().contains("The email address is already in use by another account.")){
                                message = "The email address is already in use by another account.";
                                messageToUser();

                            } else if(e.getMessage().contains("The given password is invalid. [ Password should be at least 6 characters ]")){
                                message = "The given password is invalid. [ Password should be at least 6 characters ]";
                                messageToUser();
                            }

                            Log.e("LoginActivity", "Failed Registration", e);
                          //  message.hide();
                            return;
                        }

                    }
                });


       // Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    public void messageToUser(){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void updateUI(FirebaseUser user){
        String keyId = mDatabase.push().getKey();
        char[] emailChar = userEmail.toCharArray();
        String userKey ="";

        for(int i = 0; i < emailChar.length; i++){
            if(emailChar[i] == '.'){
            }else{
                 userKey += String.valueOf(emailChar[i]);
            }
        }
        if(userFromNewAccount) {
            message = "New account created!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            mDatabase.child(userKey).child("userDetails").setValue(users);
            user = mAuth.getCurrentUser();
            Log.d("TAG", "updateUI: " + mAuth.getCurrentUser().getEmail());
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                    NewContactFragment.newInstance()).commit();
        }

        if(userFromLogin){
            message = "Welcome back!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }

}