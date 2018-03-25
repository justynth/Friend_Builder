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
    private LoginActivity context;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase database;
    private DatabaseReference userReference;

    private String firebaseUid; //initialized by listener
    private String email; //initialized by listener
    private String name; //initialized by listener
    private Current currentUser;

    private TextView description;
    private TextView nameView;
    private TextView editName;
    private TextView editDescription;

    private Button editProfile;
    private Button myEvents;
    private Button usersNearMe;
    private Button eventsNearMe;

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
    }

    /*new method*/
    private String removeInvalidKeyCharacters(String key) {
        //sanitize these characters from email
        //#$/.
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < key.length(); ++i) {
            char c = key.charAt(i);
            if (c != '#' && c != '$' && c != '/' && c != '.')
                answer.append(c);
        }

        return answer.toString();
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

        database = FirebaseDatabase.getInstance();
        userReference = database.getReference().child("User").child("University At Buffalo");

        description = findViewById(R.id.description);
        nameView = findViewById(R.id.username);
        editProfile = findViewById(R.id.editProfile);
        myEvents = findViewById(R.id.myEvents);
        usersNearMe = findViewById(R.id.usersNearMe);
        eventsNearMe = findViewById(R.id.eventsNearMe);
        editDescription = findViewById(R.id.editDescription);
        editName = findViewById(R.id.editName);;
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

                    userReference.child(removeInvalidKeyCharacters(email)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Toast.makeText(context, ""+(dataSnapshot.exists()), Toast.LENGTH_SHORT).show();
                            if (!dataSnapshot.exists()) {
                                //need user to sign up username
                                currentUser = new Current(email, name, "JEAHEA", 5, 3);

                                userReference.child(removeInvalidKeyCharacters(email)).setValue(currentUser);
                                setEverythingExceptPicAndName(View.VISIBLE);
                                //setUserNameCreation(View.VISIBLE);
                            }
                            else {
                                userReference.child(removeInvalidKeyCharacters(email)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        currentUser = dataSnapshot.getValue(Current.class);
                                        nameView.setText(currentUser.getName());
                                        description.setText(currentUser.getDescription());
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
                    nameView.setText("");
                    setEverythingExceptPicAndName(View.INVISIBLE);
                    //initializeSignOut();
                    //if (unAuthToggle) {
                        startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()
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
        editProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                description.setVisibility(View.GONE);
                nameView.setVisibility(View.GONE);
                editDescription.setVisibility(View.VISIBLE);
                editName.setVisibility(View.VISIBLE);
                editName.setText(nameView.getText().toString());
                editDescription.setText(description.getText().toString());
                editProfile.setText("Save");
                editProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        description.setText(editDescription.getText().toString());
                        nameView.setText(editName.getText().toString());
                        ArrayList<Event> temp = new ArrayList<>();
                        //temp.add(new HostedEvent(userName, "SSB4", "Competition", null, null, null, false));
                        currentUser = new Current(email,editName.getText().toString(), editDescription.getText().toString(), 3, 5);
                        userReference.child(removeInvalidKeyCharacters(email)).setValue(currentUser);
                        editProfile.setText("edit");
                        description.setVisibility(View.VISIBLE);
                        nameView.setVisibility(View.VISIBLE);
                        editName.setVisibility(View.GONE);
                        editDescription.setVisibility(View.GONE);
                        addButtonListeners();
                    }
                });
            }
        });

        myEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("user", email);

                intent.putExtra("user", bundle);

                startActivity(intent);
                finish();
            }
        });
        
         usersNearMe.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent toNearMeActivity = new Intent(context, NearbyActivity.class);
                startActivity(toNearMeActivity);
                finish();
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
                finish();
            }
        }
    }

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
}

