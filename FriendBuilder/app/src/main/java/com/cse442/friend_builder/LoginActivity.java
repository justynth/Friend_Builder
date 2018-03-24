package com.cse442.friend_builder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cse442.friend_builder.model.Current;
import com.cse442.friend_builder.model.Event;
import com.cse442.friend_builder.model.HostedEvent;
import com.cse442.friend_builder.model.Other;
import com.cse442.friend_builder.model.listeners.UserNameListener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

public class LoginActivity extends AppCompatActivity {
    /*new code*/
    private final int minimumUserNameLength = 4;
    private final int maximumUserNameLength = 9;

    private boolean unAuthToggle;
    private boolean loggedIn;

    private LoginActivity context;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private AuthUI authentication;
    private AuthUI.IdpConfig error = new AuthUI.IdpConfig.EmailBuilder().build();

    private FirebaseDatabase database;
    private DatabaseReference userNameReference;
    private DatabaseReference userReference;
    //private DatabaseReference usedForCurrentToUsersMessages;
    //private DatabaseReference usedForUsersToCurrentMessages;

    private String firebaseUid; //initialized by listener
    private String email; //initialized by listener
    private String name; //initialized by listener
    private String userName;
    private Current currentUser;

    private TextView description;
    private TextView nameView;
    private Button editProfile;
    private Button myEvents;
    private Button usersNearMe;
    private Button eventsNearMe;

    private String decided;
    private boolean valid;
    private EditText userNamePrompt;
    private Button userNameSubmit;
    private UserNameListener userNameListener;
    //login activity, profile activity, login layout, profile layout, gradle files


    /*private TextView err,SignUp;
    private EditText user,pass;
    private Button login;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editVisuals();
        initializeInstanceVariables();
        addAuthListener();
        addButtonListeners();

        findViewById(R.id.eventsNearMe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(context);
            }
        });

        /*new code end*/

        /*user = (EditText) findViewById(R.id.user);
        pass = (EditText)findViewById(R.id.pass);
        login = (Button)findViewById(R.id.login);
        err = (TextView)findViewById(R.id.err);
        SignUp = (TextView)findViewById(R.id.SignUp);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String s1 = user.getText().toString();
                String s2 = pass.getText().toString();
                validate(s1,s2);
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,UserCreation.class);
                startActivity(intent);
            }
        });*/
    }



    /*new method*/
    private boolean loggedIn(FirebaseUser user) {
        return user != null;
    }

    /*new method*/
    private void editVisuals() {
        getSupportActionBar().setTitle("Profile");
        //getSupportActionBar().hide();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /*new method*/
    private void initializeInstanceVariables() {
        context = this;

        mFirebaseAuth = FirebaseAuth.getInstance();
        authentication = AuthUI.getInstance();
        unAuthToggle = true;

        database = FirebaseDatabase.getInstance();
        userNameReference = database.getReference().child("UserNames").child("University At Buffalo");
        userReference = database.getReference().child("User").child("University At Buffalo");

        description = findViewById(R.id.description);
        nameView = findViewById(R.id.username);
        editProfile = findViewById(R.id.editProfile);
        myEvents = findViewById(R.id.myEvents);
        usersNearMe = findViewById(R.id.usersNearMe);
        eventsNearMe = findViewById(R.id.eventsNearMe);
        userNamePrompt = findViewById(R.id.userNamePrompt);
        userNameSubmit = findViewById(R.id.userNameSubmit);
    }

    private void setEverythingExceptPicAndName(int visibility) {
        findViewById(R.id.descriptionHeader).setVisibility(visibility);
        description.setVisibility(visibility);
        nameView.setVisibility(visibility);
        editProfile.setVisibility(visibility);
        myEvents.setVisibility(visibility);
        usersNearMe.setVisibility(visibility);
        eventsNearMe.setVisibility(visibility);
    }

    private void setUserNameCreation(int visibility) {
        userNamePrompt.setVisibility(visibility);
        userNameSubmit.setVisibility(visibility);
    }

    /*new method*/
    private void addAuthListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Toast.makeText(context, "Called", Toast.LENGTH_SHORT).show();
                if (loggedIn(user)) {
                    //initializeSignIn();
                    firebaseUid = user.getUid();

                    name = user.getDisplayName();

                    email = user.getEmail();

                    userNameReference.child(firebaseUid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Toast.makeText(context, ""+(dataSnapshot.exists()), Toast.LENGTH_SHORT).show();
                            if (!dataSnapshot.exists()) {
                                //need user to sign up username
                                setEverythingExceptPicAndName(View.INVISIBLE);
                                setUserNameCreation(View.VISIBLE);
                            }
                            else {
                                userReference.child(dataSnapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        currentUser = dataSnapshot.getValue(Current.class);
                                        userName = null;
                                        nameView.setText(dataSnapshot.getKey());
                                        setEverythingExceptPicAndName(View.VISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                //currentUser = dataSnapshot.getValue(Current.class);
                                //userName = null;
                                //nameView.setText(dataSnapshot.getKey());
                                //setEverythingExceptPicAndName(View.VISIBLE); //as they are already visible
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });






                    //loadDataForCurrentUser();
                }
                else {
                    //initializeSignOut();
                    //if (unAuthToggle) {
                        startActivityForResult(authentication
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList(
                                        error
                                ))
                                .build(), 1);
                        //unAuthToggle = !unAuthToggle;
                        //ask for GPS permissions
                    //}
                }

                //finish();

            }
        };
    }

    private void addButtonListeners() {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Event> temp = new ArrayList<>();
                temp.add(new HostedEvent(userName, "SSB4", "Competition", null, null, null, false));
                currentUser = new Current(email, userName, "name", temp);
                userReference.child(firebaseUid).setValue(currentUser);
            }
        });
        userNameSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameListener = new UserNameListener();
                valid = true;
                decided = userNamePrompt.getText().toString();
                if (decided.length() == 0) {
                    Toast.makeText(context, "You need a username to continue", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                else if (decided.length() >= minimumUserNameLength && decided.length() <= maximumUserNameLength){
                    userReference.addChildEventListener(userNameListener);
                }
                else {
                    Toast.makeText(context, "Usernames need to be at least" + minimumUserNameLength +
                            "characters and at most" + maximumUserNameLength + "characters", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if (valid) {
                    //check if key is null and if it is, then add
                    userReference.child(decided).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                currentUser = new Current(email, null, name, new ArrayList<Event>());
                                userNameReference.child(firebaseUid).setValue(decided);
                                userReference.child(decided).setValue(currentUser);
                                setEverythingExceptPicAndName(View.VISIBLE);
                                setUserNameCreation(View.GONE);

                            }
                            else {
                                Toast.makeText(context, userReference.child(decided).getKey().toString(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(context, "Username already taken", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    /*andler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> existingUserNames = userNameListener.getExistingUserNames();
                            for (int i = 0; i < existingUserNames.size(); ++i) {
                                if (existingUserNames.get(i).equals(decided)) {
                                    Toast.makeText(context, existingUserNames.get(i), Toast.LENGTH_SHORT).show();
                                    valid = false;
                                }
                            }

                            if (valid) {
                                userName = decided;
                                currentUser.setUserName(decided);
                                userReference.child(firebaseUid).setValue(currentUser);
                                setUserNameCreation(View.GONE);
                            }
                        }
                    }, 5000);*/
                }
            }
        });
    }

    /*private void validate(String u,String p){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = shared.getString(u,"");

        if(u.equals("Admin") && p.equals("Admin")){
            Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
            startActivity(intent);
        }else if(user.equals(p))
        {
            Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
            startActivity(intent);
        }
        else if(!user.equalsIgnoreCase("")){
            err.setText("Email and Password do not match");
        }
        else{
            err.setText("Email and Password do not match");
            // err.setText(data.getMap().get(u));
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(context, "Cancelled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /*@Override
    public void onDestroy() {
        Toast.makeText(context, "ONDESTROY CALLED", Toast.LENGTH_SHORT).show();
        mFirebaseAuth.signOut();
        finish();
        super.onDestroy();
    }*/

    /*new method*/
    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    /*new method*/
    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    /*@Override
    public void onBackPressed() {
        mFirebaseAuth.signOut();
        finish();
    }*/
}
