package com.example.watsonashton_helpertracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.watsonashton_helpertracker.fragments.HomeScreenFragment;
import com.example.watsonashton_helpertracker.fragments.LogInFragment;
import com.example.watsonashton_helpertracker.fragments.NewContactFragment;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LifecycleObserver,LogInFragment.LogInListener, SignUpFragment.SignUpListener, NewContactFragment.NewContactListener, HomeScreenFragment.HomeScreenListener, LocationListener {
private FirebaseDatabase database;
private DatabaseReference mDatabase;
private FirebaseAuth mAuth;
private static final int PERMISSION_SEND_SMS = 123;
private static final  int REQUEST_LOCATION_PERMISSIONS = 0x01001;
LocationManager mLocationManger;
String message;
Context mContext;
String userEmail;
String TAG = "TAG";
Boolean userFromLogin;
Boolean userFromNewAccount;
String testRunFirstName;
String testRunLastName;
String masterUserKey;
Boolean positiveButtonPushed;
Boolean signalButtonIsActive;
boolean mRequestingUpdates = false;
Boolean locationRequestFromUI = false;


HashMap<String, String> users = new HashMap<String, String>();
HashMap<String, String> contacts = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        mLocationManger = (LocationManager)getSystemService(LOCATION_SERVICE);

      // getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
       //        LogInFragment.newInstance()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
               HomeScreenFragment.newInstance()).commit();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String name = user.getEmail();
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        }else{

        }
        requestLocationPermissions();

      /*  mDatabase.child("abjdj2@gmailcom/").child("contacts/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot t: snapshot.getChildren()){
                    Toast.makeText(getApplicationContext(),t.getKey().toString() , Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),t.getValue().toString() , Toast.LENGTH_SHORT).show();
                }

                /*for(int i =0; i< snapshot.getChildren())
                Log.e("some", "======="+snapshot.getChildren().iterator().next().getValue());
                Toast.makeText(getApplicationContext(),snapshot.getChildren().iterator().next().getKey().toString() , Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),snapshot.getChildren().iterator().next().getValue().toString() , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/



    }
    private  void requestLocationPermissions(){
       if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this,
                   new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                  REQUEST_LOCATION_PERMISSIONS);
       }
    }
    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        } else {
            // permission already granted run sms send
          //  TrailTextMessage();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted

                } else {
                    // permission denied
                    requestSmsPermission();
                }
                return;

    }
public void TrailTextMessage(String phone, String message){
    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
   // PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
    SmsManager sms=SmsManager.getDefault();
    sms.sendTextMessage(phone, null, message, null,null);


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
        testRunFirstName = fname;
        testRunLastName = lname;
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
    @Override
    public void newContactFieldsEmpty() {
        Toast.makeText(this, "Please fill out all fields to continue", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void SignalButtonPushed() {
      // if(!signalButtonIsActive){
      //     Toast.makeText(this, "Click the 'Stop Signal' button for signal to dismiss.", Toast.LENGTH_SHORT).show();
      // }else {
        Button stop  = findViewById(R.id.buttonStopSignal);
        ImageView redButton = findViewById(R.id.imageViewStartSignal);
        TextView info = findViewById(R.id.textViewInsturcutons);
        info.setText(R.string.push_the_red_button_in_case_of_emergency);

        stop.setEnabled(true);
        redButton.setEnabled(false);
           signalButtonIsActive = true;
           Toast.makeText(this, "WORKING!!!!", Toast.LENGTH_SHORT).show();
           locationRequestFromUI = true;

           if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !mRequestingUpdates) {
               mLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10.0f, this);
               mRequestingUpdates = true;
           }
    //   }

    }

    @Override
    public void StopSignalButtonHasBeenPushed() {
        Button stop  = findViewById(R.id.buttonStopSignal);
        ImageView redButton = findViewById(R.id.imageViewStartSignal);
        TextView info = findViewById(R.id.textViewInsturcutons);
        info.setText(R.string.push_the_red_button_in_case_of_emergency);
        Toast.makeText(this, "Broadcasts have stop being sent out", Toast.LENGTH_SHORT).show();
        stop.setEnabled(false);
        redButton.setEnabled(true);
    }

    @Override
    public void UserLogOut() {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Are you sure you want to Log Out?");
        dlgAlert.setTitle("Log Out");
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "You have log out//ADD LOG LAST!", Toast.LENGTH_SHORT).show();
                        //LOG OUT HERE
                    }
                });
        dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You didn't log out", Toast.LENGTH_SHORT).show();
            }
        });
        dlgAlert.create().show();
    }

    @Override
    public void newContactReadyToAdd(String f_name, String l_name, String phoneNum) {

        requestSmsPermission();
        if(phoneNum.length() != 10 || phoneNum.contains("(")|| phoneNum.contains(")") || phoneNum.contains("-") || phoneNum.contains("_")){
            Toast.makeText(this, "Not a valid number please use this format: 123456789", Toast.LENGTH_SHORT).show();
        }else{

            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Are you sure "+phoneNum+ " is the correct number?\n"+f_name+" will be added to your emergency contacts list if you click ok.");
            dlgAlert.setTitle("HelperTracker");

            dlgAlert.setCancelable(true);

            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            positiveButtonPushed = true;
                            //dismiss the dialog
                            String message1 = "This is a practice signal sent by "+testRunFirstName+" "+ testRunLastName+" through TrackerHelper";
                            String message2 = f_name+" if "+testRunFirstName+ " is ever in trouble you'll receive a text message similar to this with his/her current location.";
                            String message3 = "No further action is needed at this time. Thank you.";

                            TrailTextMessage(phoneNum, message1);
                            TrailTextMessage(phoneNum, message2);
                            TrailTextMessage(phoneNum, message3);
                            String fullName = f_name+ " "+ l_name;

                            contacts.put(fullName, phoneNum);
                            mDatabase.child(masterUserKey).child("contacts").setValue(contacts);
                            Toast.makeText(getApplicationContext(), "Signal has been sent out, please check with the receiver to confirmed.", Toast.LENGTH_SHORT).show();

                           // Toast.makeText(t, phoneNum, Toast.LENGTH_SHORT).show();

                        }
                    });
            dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positiveButtonPushed = false;
                    Toast.makeText(getApplicationContext(), "Contact was not saved.", Toast.LENGTH_SHORT).show();
                }
            });
            //  requestSmsPermission();
            // exampleTextMessage();

            dlgAlert.create().show();



        }
    }

    public void createNewUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull  Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("TAG", "success");
                            FirebaseUser user = mAuth.getCurrentUser();
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
                            return;
                        }
                    }
                });
    }
    public void messageToUser(){ Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }

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
            masterUserKey = userKey;
            user = mAuth.getCurrentUser();
            Log.d("TAG", "updateUI: " + mAuth.getCurrentUser().getEmail());
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
                    NewContactFragment.newInstance()).commit();
        }

        if(userFromLogin){
            masterUserKey = userKey;
            message = "Welcome back!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        String address = "";
        String city = "";
        String state = "";
        String country;
        String postalCode = "";
        String knownName;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(locationRequestFromUI){


            locationRequestFromUI = false;
            TextView lat = findViewById(R.id.textViewLatFillIn);
            TextView lon = findViewById(R.id.textViewLonFillIn);
            TextView addy = findViewById(R.id.textViewAddressFillIn);
            TextView info = findViewById(R.id.textViewInsturcutons);
            Button stop  = findViewById(R.id.buttonStopSignal);
            ImageView redButton = findViewById(R.id.imageViewStartSignal);

            stop.setEnabled(true);


            String latText = Double.toString(location.getLatitude());
            String lonText = Double.toString(location.getLongitude());

           // String addyText = address + "\n"+city+" "+state+" "+postalCode;
            String addyText = address;
            lon.setText(lonText);
            lat.setText(latText);
            addy.setText(addyText);
            info.setText(R.string.stop_singal);



            if(mRequestingUpdates){
                mRequestingUpdates = false;
                mLocationManger.removeUpdates(this);
            }

        }

    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}