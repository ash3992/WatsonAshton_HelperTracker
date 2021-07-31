package com.example.watsonashton_helpertracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleObserver;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.os.SystemClock;
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
import com.example.watsonashton_helpertracker.objects.Contacts;
import com.example.watsonashton_helpertracker.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LifecycleObserver,LogInFragment.LogInListener, SignUpFragment.SignUpListener, NewContactFragment.NewContactListener, HomeScreenFragment.HomeScreenListener, LocationListener,ContactsListFragment.ContactListener, AddNewContactFragment.AddNewContactFragmentListener, DetailContactFragment.DetailContactFragmentListener, EditFragment.EditFragmentListener {
private FirebaseDatabase database;
private DatabaseReference mDatabase;
private FirebaseAuth mAuth;
private static final int PERMISSION_SEND_SMS = 123;
private static final  int REQUEST_LOCATION_PERMISSIONS = 0x01001;
private NotificationManager mNotificationManager;
private static final int NOTIFICATION_ID = 0;
private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
long repeatInterval = 1*60*1000;
long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;
private static final ArrayList<Contacts> contactsLog = new ArrayList<>();
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
Boolean appCurrentlyOpen;
Boolean locationRequestFromUI = false;
User userDetails;
Boolean firstMessageBeingSent = false;
HashMap<String, String> users = new HashMap<String, String>();
HashMap<String, String> contacts = new HashMap<String, String>();
Intent notifyIntent;
boolean alarmUp;
PendingIntent notifyPendingIntent;
AlarmManager alarmManager;
ContactsAdapter contactsAdapter;
int  currentContactSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        mLocationManger = (LocationManager)getSystemService(LOCATION_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyIntent = new Intent(this, AlarmReceiver.class);
        alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        notifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

      // getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
       //        LogInFragment.newInstance()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer,
               HomeScreenFragment.newInstance()).commit();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String name = user.getEmail();
            char[] emailChar = name.toCharArray();
            String userKey ="";

            for(int i = 0; i < emailChar.length; i++){
                if(emailChar[i] == '.'){
                }else{
                    userKey += String.valueOf(emailChar[i]);
                }
            }
            masterUserKey = userKey;
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        }else{

        }
        requestLocationPermissions();
        GrabContactsAndUserInfo();

        AlarmReceiver.OnNewLocationListener onNewLocationListener = new AlarmReceiver.OnNewLocationListener() {
            @Override
            public void onNewLocationReceived(String location) {
                // do something
                Toast.makeText(MainActivity.this, location, Toast.LENGTH_SHORT).show();
                        LocationFinder();
            }
        };

        // start listening for new location
        AlarmReceiver.setOnNewLocationListener(onNewLocationListener);

    }
    public void LocationFinder() {
        if(appCurrentlyOpen){

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !mRequestingUpdates) {
                mLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10.0f, this);
                mRequestingUpdates = true;
                locationRequestFromUI = true;
            }
        }else if(!appCurrentlyOpen){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !mRequestingUpdates) {
                mLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10.0f, this);
                mRequestingUpdates = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appCurrentlyOpen = true;
        /*
        TextView info = findViewById(R.id.editTextFirstContactPhoneNumber);
       info.setText("Something");*/

    }


    @Override
    protected void onPause() {
        super.onPause();
        appCurrentlyOpen = false;
    }

    private void GrabContactsAndUserInfo(){
        contactsLog.clear();
      //  masterUserKey+"/"
        mDatabase.child(masterUserKey+"/").child("contacts/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Contacts contacts = new Contacts(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
                    contactsLog.add(contacts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child(masterUserKey+"/").child("userDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            String firstName = "";
            String lastName = "";
            String eyes = "";
            String hair = "";
            String height = "";
            String weight = "";

            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                    firstName = (String) snapshot.child("First Name").getValue();
                    lastName = (String) snapshot.child("Last Name").getValue();
                    eyes = (String) snapshot.child("Eyes").getValue();
                    hair = (String) snapshot.child("Hair").getValue();
                    height = (String) snapshot.child("Height").getValue();
                    weight = (String) snapshot.child("Weight").getValue();
                    userDetails = new User(eyes,firstName,hair,height,lastName,weight);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show(); }

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
                    { throw task.getException(); }
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
        firstMessageBeingSent = true;
        Button stop  = findViewById(R.id.buttonStopSignal);
        ImageView redButton = findViewById(R.id.imageViewStartSignal);
        TextView info = findViewById(R.id.textViewInsturcutons);
        info.setText(R.string.push_the_red_button_in_case_of_emergency);
        stop.setEnabled(true);
        redButton.setEnabled(false);
        signalButtonIsActive = true;
        Toast.makeText(this, "WORKING!!!!", Toast.LENGTH_SHORT).show();
        locationRequestFromUI = true;


           if(contactsLog.size() == 0){
               Toast.makeText(this, "Please add a contact before continuing.", Toast.LENGTH_SHORT).show();
           }else{
               if(appCurrentlyOpen){
                   if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !mRequestingUpdates) {
                       if(firstMessageBeingSent){
                           FirstTextMessage(userDetails, contactsLog);
                       }
                       mLocationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10.0f, this);
                       mRequestingUpdates = true;
                       MessageTimer();

                   }

               }
           }


    }

    public void MessageTimer(){
        if (alarmManager != null) {
            alarmManager.setInexactRepeating
                    (AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);
        }
    }

    @Override
    public void StopSignalButtonHasBeenPushed() {
        firstMessageBeingSent = false;
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
                        //LOG OUT HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
    public void UserClickContactsList() {
        contactsAdapter = new ContactsAdapter(contactsLog,this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, ContactsListFragment.newInstance(contactsAdapter))
                .addToBackStack("")
                .commit();
    }

    @Override
    public void addContactFieldsEmpty() {
        Toast.makeText(this, "Please fill out all fields to continue", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addContactReadyToAdd(String f_name, String l_name, String phoneNum) {
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

                            String fullName = f_name+ " "+ l_name;

                            if(contactsLog.size() == 0){
                                HashMap<String, String> contacts = new HashMap<String, String>();
                                contacts.put(fullName, phoneNum);
                                mDatabase.child(masterUserKey).child("contacts").setValue(contacts);
                                contactsLog.clear();
                                for (String i : contacts.keySet()) {
                                    Contacts m = new Contacts(i, contacts.get(i));
                                    contactsLog.add(m);
                                }
                            }else if(contactsLog.size() == 1){
                                HashMap<String, String> contacts = new HashMap<String, String>();
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                contacts.put(fullName, phoneNum);
                                mDatabase.child(masterUserKey).child("contacts").setValue(contacts);
                                contactsLog.clear();
                                for (String i : contacts.keySet()) {
                                    Contacts m = new Contacts(i, contacts.get(i));
                                    contactsLog.add(m);
                                }
                            }else if(contactsLog.size() == 2){
                                HashMap<String, String> contacts = new HashMap<String, String>();
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                contacts.put(fullName, phoneNum);
                                mDatabase.child(masterUserKey).child("contacts").setValue(contacts);
                                contactsLog.clear();
                                for (String i : contacts.keySet()) {
                                    Contacts m = new Contacts(i, contacts.get(i));
                                    contactsLog.add(m);
                                }

                            }
                            getSupportFragmentManager().popBackStack();
                        }
                    });
            dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  //  positiveButtonPushed = false;
                    Toast.makeText(getApplicationContext(), "Contact was not saved.", Toast.LENGTH_SHORT).show();
                }
            });
            dlgAlert.create().show();

        }
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
                            GrabContactsAndUserInfo();

                        }
                    });
            dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positiveButtonPushed = false;
                    Toast.makeText(getApplicationContext(), "Contact was not saved.", Toast.LENGTH_SHORT).show();
                }
            });
            dlgAlert.create().show();

        }
    }

    @Override
    public void ContactAdd() {

        if(contactsLog.size() >= 3){
            Toast.makeText(getApplicationContext(), "No more then three contacts are allowed to be added", Toast.LENGTH_SHORT).show();
        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, AddNewContactFragment.newInstance())
                    .addToBackStack("")
                    .commit();
        }

    }

    @Override
    public void EditContact() {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, EditFragment.newInstance(contactsLog.get(currentContactSelected)))
                .addToBackStack("")
                .commit();

    }

    @Override
    public void TrashContact(String firstName, String lastName, String phoneNum) {
//

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Are you sure you want to delete this contact?");
        dlgAlert.setTitle("HelperTracker");
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String fullName = firstName+ " "+ lastName;

                        if(currentContactSelected == 0){
                            HashMap<String, String> contacts = new HashMap<String, String>();
                            Contacts editContact = new Contacts(fullName, phoneNum);
                            contactsLog.remove(0);

                            if(contactsLog.size() == 1){
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                            }else if(contactsLog.size() == 2){
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                            }else if(contactsLog.size() == 3){
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                contacts.put(contactsLog.get(2).getFullName(),contactsLog.get(2).getPhoneNum());
                            }

                            mDatabase.child(masterUserKey).child("contacts").setValue(contacts);


                        }else if(currentContactSelected == 1){
                            HashMap<String, String> contacts = new HashMap<String, String>();
                            Contacts editContact = new Contacts(fullName, phoneNum);
                            contactsLog.remove(1);

                            if(contactsLog.size() == 1){
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                            }else if(contactsLog.size() == 2){
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                            }else if(contactsLog.size() == 3){
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                contacts.put(contactsLog.get(2).getFullName(),contactsLog.get(2).getPhoneNum());
                            }

                            mDatabase.child(masterUserKey).child("contacts").setValue(contacts);
                        }else if(currentContactSelected == 2){
                            HashMap<String, String> contacts = new HashMap<String, String>();
                            Contacts editContact = new Contacts(fullName, phoneNum);
                            contactsLog.remove(2);

                            if(contactsLog.size() == 1){
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                            }else if(contactsLog.size() == 2){
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                            }else if(contactsLog.size() == 3){
                                contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                contacts.put(contactsLog.get(2).getFullName(),contactsLog.get(2).getPhoneNum());
                            }

                            mDatabase.child(masterUserKey).child("contacts").setValue(contacts);

                        }


                        Toast.makeText(getApplicationContext(), "Contact was deleted.", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().popBackStack();
                    }
                });
        dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Contact was not deleted.", Toast.LENGTH_SHORT).show();
            }
        });
        dlgAlert.create().show();

    }

    @Override
    public void EditContactFieldEmpty() {
        Toast.makeText(this, "Please fill out all fields to continue", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void EditContactIsReadyToAdd(String firstName, String lastName, String phoneNum) {
        if(phoneNum.length() != 10 || phoneNum.contains("(")|| phoneNum.contains(")") || phoneNum.contains("-") || phoneNum.contains("_")){
            Toast.makeText(this, "Not a valid number please use this format: 123456789", Toast.LENGTH_SHORT).show();
        }else{

            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Are you sure "+phoneNum+ " is the correct number?\n"+firstName+" will be added to your emergency contacts list if you click ok.");
            dlgAlert.setTitle("HelperTracker");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            String fullName = firstName+ " "+ lastName;

                            if(currentContactSelected == 0){
                                HashMap<String, String> contacts = new HashMap<String, String>();
                                Contacts editContact = new Contacts(fullName, phoneNum);
                                contactsLog.set(0, editContact);

                                if(contactsLog.size() == 1){
                                    contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                }else if(contactsLog.size() == 2){
                                    contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                    contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                }else if(contactsLog.size() == 3){
                                    contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                    contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                    contacts.put(contactsLog.get(2).getFullName(),contactsLog.get(2).getPhoneNum());
                                }

                                mDatabase.child(masterUserKey).child("contacts").setValue(contacts);


                            }else if(currentContactSelected == 1){
                                HashMap<String, String> contacts = new HashMap<String, String>();
                                Contacts editContact = new Contacts(fullName, phoneNum);
                                contactsLog.set(1, editContact);

                                if(contactsLog.size() == 1){
                                    contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                }else if(contactsLog.size() == 2){
                                    contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                    contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                }else if(contactsLog.size() == 3){
                                    contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                    contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                    contacts.put(contactsLog.get(2).getFullName(),contactsLog.get(2).getPhoneNum());
                                }

                                mDatabase.child(masterUserKey).child("contacts").setValue(contacts);
                            }else if(currentContactSelected == 2){
                                HashMap<String, String> contacts = new HashMap<String, String>();
                                Contacts editContact = new Contacts(fullName, phoneNum);
                                contactsLog.set(2, editContact);

                                if(contactsLog.size() == 1){
                                    contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                }else if(contactsLog.size() == 2){
                                    contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                    contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                }else if(contactsLog.size() == 3){
                                    contacts.put(contactsLog.get(0).getFullName(),contactsLog.get(0).getPhoneNum());
                                    contacts.put(contactsLog.get(1).getFullName(),contactsLog.get(1).getPhoneNum());
                                    contacts.put(contactsLog.get(2).getFullName(),contactsLog.get(2).getPhoneNum());
                                }

                                mDatabase.child(masterUserKey).child("contacts").setValue(contacts);

                            }
                            Toast.makeText(getApplicationContext(), "Contact was edited and save", Toast.LENGTH_SHORT).show();


                            FragmentManager manager = getSupportFragmentManager();
                            if (manager.getBackStackEntryCount() > 0) {
                                FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
                                manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        }
                    });
            dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Contact was not saved.", Toast.LENGTH_SHORT).show();
                }
            });
            dlgAlert.create().show();

        }
    }

    @Override
    public void ContactListItemWasClicked(int contactPosition) {

        currentContactSelected = contactPosition;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, DetailContactFragment.newInstance(contactsLog.get(contactPosition)))
                .addToBackStack("")
                .commit();
    }

    public void FirstTextMessage( User user, ArrayList<Contacts> contacts){

        if(contacts.size() == 1){
            String userInfo = "Name: "+user.getFirstName()+" "+user.getLastName()+"\nEye Color: "+ user.getEyes()+"\nHair Color: "+user.getHair()+"\n Height: "+user.getHeight()+ "\nWeight: "+user.getWeight();
            contacts.get(0).getFullName();
            contacts.get(0).getPhoneNum();

            SmsManager sms=SmsManager.getDefault();
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, "***ATTENTION***", null,null);
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, userDetails.getFirstName()+" might be in some kind of danger and has press an emergency alert through the app HelperTracker", null,null);
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, userInfo, null,null);
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, contacts.get(0).getFullName()+" we'll keep you up to date with "+user.getFirstName()+"'s current location.", null,null);

        }else if(contacts.size() == 2){
            String userInfo = "Name: "+user.getFirstName()+" "+user.getLastName()+"\nEye Color: "+ user.getEyes()+"\nHair Color: "+user.getHair()+"\n Height: "+user.getHeight()+ "\nWeight: "+user.getWeight();
            contacts.get(0).getFullName();
            contacts.get(0).getPhoneNum();

            SmsManager sms=SmsManager.getDefault();
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, "***ATTENTION***", null,null);
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, userDetails.getFirstName()+" might be in some kind of danger and has press an emergency alert through the app HelperTracker", null,null);
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, userInfo, null,null);
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, contacts.get(0).getFullName()+" we'll keep you up to date with "+user.getFirstName()+"'s current location.", null,null);

            String userInfo1 = "Name: "+user.getFirstName()+" "+user.getLastName()+"\nEye Color: "+ user.getEyes()+"\nHair Color: "+user.getHair()+"\n Height: "+user.getHeight()+ "\nWeight: "+user.getWeight();
            contacts.get(1).getFullName();
            contacts.get(1).getPhoneNum();

            SmsManager sms1=SmsManager.getDefault();
            sms1.sendTextMessage(contacts.get(1).getPhoneNum(), null, "***ATTENTION***", null,null);
            sms1.sendTextMessage(contacts.get(1).getPhoneNum(), null, userDetails.getFirstName()+" might be in some kind of danger and has press an emergency alert through the app HelperTracker", null,null);
            sms1.sendTextMessage(contacts.get(1).getPhoneNum(), null, userInfo1, null,null);
            sms1.sendTextMessage(contacts.get(1).getPhoneNum(), null, contacts.get(1).getFullName()+" we'll keep you up to date with "+user.getFirstName()+"'s current location.", null,null);

        }else if(contacts.size() == 3){
            String userInfo = "Name: "+user.getFirstName()+" "+user.getLastName()+"\nEye Color: "+ user.getEyes()+"\nHair Color: "+user.getHair()+"\n Height: "+user.getHeight()+ "\nWeight: "+user.getWeight();
            contacts.get(0).getFullName();
            contacts.get(0).getPhoneNum();

            SmsManager sms=SmsManager.getDefault();
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, "***ATTENTION***", null,null);
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, userDetails.getFirstName()+" might be in some kind of danger and has press an emergency alert through the app HelperTracker", null,null);
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, userInfo, null,null);
            sms.sendTextMessage(contacts.get(0).getPhoneNum(), null, contacts.get(0).getFullName()+" we'll keep you up to date with "+user.getFirstName()+"'s current location.", null,null);

            String userInfo1 = "Name: "+user.getFirstName()+" "+user.getLastName()+"\nEye Color: "+ user.getEyes()+"\nHair Color: "+user.getHair()+"\n Height: "+user.getHeight()+ "\nWeight: "+user.getWeight();
            contacts.get(1).getFullName();
            contacts.get(1).getPhoneNum();

            SmsManager sms1=SmsManager.getDefault();
            sms1.sendTextMessage(contacts.get(1).getPhoneNum(), null, "***ATTENTION***", null,null);
            sms1.sendTextMessage(contacts.get(1).getPhoneNum(), null, userDetails.getFirstName()+" might be in some kind of danger and has press an emergency alert through the app HelperTracker", null,null);
            sms1.sendTextMessage(contacts.get(1).getPhoneNum(), null, userInfo1, null,null);
            sms1.sendTextMessage(contacts.get(1).getPhoneNum(), null, contacts.get(1).getFullName()+" we'll keep you up to date with "+user.getFirstName()+"'s current location.", null,null);

            String userInfo2 = "Name: "+user.getFirstName()+" "+user.getLastName()+"\nEye Color: "+ user.getEyes()+"\nHair Color: "+user.getHair()+"\n Height: "+user.getHeight()+ "\nWeight: "+user.getWeight();
            contacts.get(1).getFullName();
            contacts.get(1).getPhoneNum();

            SmsManager sms2=SmsManager.getDefault();
            sms2.sendTextMessage(contacts.get(2).getPhoneNum(), null, "***ATTENTION***", null,null);
            sms2.sendTextMessage(contacts.get(2).getPhoneNum(), null, userDetails.getFirstName()+" might be in some kind of danger and has press an emergency alert through the app HelperTracker", null,null);
            sms2.sendTextMessage(contacts.get(2).getPhoneNum(), null, userInfo1, null,null);
            sms2.sendTextMessage(contacts.get(2).getPhoneNum(), null, contacts.get(2).getFullName()+" we'll keep you up to date with "+user.getFirstName()+"'s current location.", null,null);
        }

    }

    public void SendOutLocation(String lat, String lon, String addy){
        if(contactsLog.size() == 1){
            SmsManager sms=SmsManager.getDefault();
            sms.sendTextMessage( contactsLog.get(0).getPhoneNum(), null, "Current Known Location\nLongitude: "+ lon+"\nLatitude: "+lat+"\nAddress: "+addy, null,null);

        }else if(contactsLog.size() == 2){
            SmsManager sms=SmsManager.getDefault();
            sms.sendTextMessage( contactsLog.get(0).getPhoneNum(), null, "Current Known Location\nLongitude: "+ lon+"\nLatitude: "+lat+"\nAddress: "+addy, null,null);
            SmsManager sms1=SmsManager.getDefault();
            sms1.sendTextMessage( contactsLog.get(1).getPhoneNum(), null, "Current Known Location\nLongitude: "+ lon+"\nLatitude: "+lat+"\nAddress: "+addy, null,null);

        }else if(contactsLog.size() == 3){
            SmsManager sms=SmsManager.getDefault();
            sms.sendTextMessage( contactsLog.get(0).getPhoneNum(), null, "Current Known Location\nLongitude: "+ lon+"\nLatitude: "+lat+"\nAddress: "+addy, null,null);
            SmsManager sms1=SmsManager.getDefault();
            sms1.sendTextMessage( contactsLog.get(1).getPhoneNum(), null, "Current Known Location\nLongitude: "+ lon+"\nLatitude: "+lat+"\nAddress: "+addy, null,null);
            SmsManager sms2=SmsManager.getDefault();
            sms2.sendTextMessage( contactsLog.get(2).getPhoneNum(), null, "Current Known Location\nLongitude: "+ lon+"\nLatitude: "+lat+"\nAddress: "+addy, null,null);
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
            GrabContactsAndUserInfo();
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

            Date currentTime = Calendar.getInstance().getTime();

            locationRequestFromUI = false;
            TextView lat = findViewById(R.id.textViewLatFillIn);
            TextView lon = findViewById(R.id.textViewLonFillIn);
            TextView addy = findViewById(R.id.textViewAddressFillIn);
            TextView info = findViewById(R.id.textViewInsturcutons);
            TextView time = findViewById(R.id.textViewLastUpdateTimeFillIn);
            Button stop  = findViewById(R.id.buttonStopSignal);
            ImageView redButton = findViewById(R.id.imageViewStartSignal);

            stop.setEnabled(true);


            String latText = Double.toString(location.getLatitude());
            String lonText = Double.toString(location.getLongitude());
            String addyText = address;
            time.setText(currentTime.toString());
            lon.setText(lonText);
            lat.setText(latText);
            addy.setText(addyText);
            info.setText(R.string.stop_singal);
            SendOutLocation(latText, lonText, addyText);


            if(mRequestingUpdates){
                mRequestingUpdates = false;
                mLocationManger.removeUpdates(this);
            }

        }

        if(appCurrentlyOpen == false){
            String latText = Double.toString(location.getLatitude());
            String lonText = Double.toString(location.getLongitude());
            String addyText = address;
            SendOutLocation(latText, lonText, addyText);
            if(mRequestingUpdates){
                mRequestingUpdates = false;
                mLocationManger.removeUpdates(this);
            }

        }

    }
    @Override
    public void onProviderEnabled(@NonNull String provider) { }

    @Override
    public void onProviderDisabled(@NonNull String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }



}