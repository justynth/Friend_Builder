package com.cse442.friend_builder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

public class LoginActivity extends AppCompatActivity {
    /*new code*/
    private boolean unAuthToggle;
    private boolean loggedIn;

    private LoginActivity context;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase database;
    private DatabaseReference userReference;
    //private DatabaseReference usedForCurrentToUsersMessages;
    //private DatabaseReference usedForUsersToCurrentMessages;

    private String firebaseUid; //initialized by listener
    private String email; //initialized by listener
    private String userName; //initialized by listener
    private Current currentUser;

    private TextView description;
    private TextView name;
    private TextView editname;
    private Button editProfile;
    private Button myEvents;
    private Button usersNearMe;
    private Button eventsNearMe;
    private TextView editor;
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
    }

    private void loadDataForCurrentUser() {
        userReference.child(firebaseUid).equalTo(firebaseUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(context, dataSnapshot.getKey().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        unAuthToggle = true;

        database = FirebaseDatabase.getInstance();
        userReference = database.getReference().child("Users").child("University At Buffalo");

        description = findViewById(R.id.description);
        name = findViewById(R.id.username);
        editProfile = findViewById(R.id.editProfile);
        myEvents = findViewById(R.id.myEvents);
        usersNearMe = findViewById(R.id.usersNearMe);
        eventsNearMe = findViewById(R.id.eventsNearMe);
        editor = findViewById(R.id.editor);
        editname = findViewById(R.id.editname);
    }

    private void setEverythingVisibleExceptPicAndName() {
        findViewById(R.id.descriptionHeader).setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        editProfile.setVisibility(View.VISIBLE);
        myEvents.setVisibility(View.VISIBLE);
        usersNearMe.setVisibility(View.VISIBLE);
        eventsNearMe.setVisibility(View.VISIBLE);
    }

    /*new method*/
    private void addAuthListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Toast.makeText(context, "Called", Toast.LENGTH_SHORT).show();
                if (loggedIn(user)) {
                    setEverythingVisibleExceptPicAndName(); //as they are already visible

                    firebaseUid = user.getUid();

                    userName = user.getDisplayName();

                    email = user.getEmail();





                    userReference.child(firebaseUid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                currentUser = new Current(email, userName, "userChangesLater", new ArrayList<Event>(),"test");
                                userReference.child(firebaseUid).setValue(currentUser);
                            }
                            else {
                                Toast.makeText(context, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                currentUser = dataSnapshot.getValue(Current.class);
                                name.setText(currentUser.getUserName());
                                description.setText(currentUser.getDescription());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                    //loadDataForCurrentUser();
                }
                else {
                    if (unAuthToggle) {
                        startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()
                                ))
                                .build(), 1);
                        unAuthToggle = !unAuthToggle;
                        //ask for GPS permissions
                    }
                }

                //finish();

            }
        };

        //add the listener to the auth object
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void addButtonListeners() {
        /*editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Event> temp = new ArrayList<>();
                temp.add(new HostedEvent(userName, "SSB4", "Competition", null, null, null, false));
                currentUser = new Current(email, userName, "name", temp);
                userReference.child(firebaseUid).setValue(currentUser);
            }
        });*/
        editProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                description.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                editor.setVisibility(View.VISIBLE);
                editname.setVisibility(View.VISIBLE);
                editname.setText(name.getText().toString());
                editor.setText(description.getText().toString());
                editProfile.setText("Save");
                editProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        description.setText(editor.getText().toString());
                        name.setText(editname.getText().toString());
                        ArrayList<Event> temp = new ArrayList<>();
                        temp.add(new HostedEvent(userName, "SSB4", "Competition", null, null, null, false));
                        currentUser = new Current(email,editname.getText().toString(),"name",temp,editor.getText().toString());
                        userReference.child(firebaseUid).setValue(currentUser);
                        editProfile.setText("edit");
                        description.setVisibility(View.VISIBLE);
                        name.setVisibility(View.VISIBLE);
                        editname.setVisibility(View.GONE);
                        editor.setVisibility(View.GONE);
                        addButtonListeners();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(context, "Cancelled!", Toast.LENGTH_SHORT).show();
                mFirebaseAuth.signOut();
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFirebaseAuth.signOut();
        finish();
    }

    /*new method*/
    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(context, "THIS IS A RESUME!", Toast.LENGTH_SHORT).show();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    /*new method*/
    @Override
    public void onPause() {
        super.onPause();
        //Toast.makeText(context, "THIS IS AN ON PAUSE", Toast.LENGTH_SHORT).show();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        mFirebaseAuth.signOut();
        finish();
    }
}
