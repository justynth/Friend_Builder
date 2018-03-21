package com.cse442.friend_builder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cse442.friend_builder.model.Current;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    /*new instance variables*/
    private ProfileActivity context;

    private String uid;
    private String userName;
    private String displayName;
    private String email;
    private String description;

    private FirebaseDatabase database;
    private DatabaseReference currentData;
    private DatabaseReference currentToUsers; //subject to change
    private DatabaseReference usersToCurrent; //subject to change

    private Button editProfile;
    private TextView name;
    /*new instance variables end*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*new code begin*/
        //visuals
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //instance variable
        context = this;
        editProfile = (Button) findViewById(R.id.edit_profile);
        name = (TextView) findViewById(R.id.username);

        database = FirebaseDatabase.getInstance();

        //load the user's information
        Intent fromLoginScreen = getIntent(); Bundle bundle = fromLoginScreen.getBundleExtra("identity");

        ArrayList<String> identity = bundle.getStringArrayList("identity");
        uid = identity.get(0);
        displayName = identity.get(1); name.setText(displayName);
        email = identity.get(2);
            //using uid, get other information for this user?
        currentData = database.getReference()
                .child("Users")
                .child("University At Buffalo");
        //check if user exists in the database
        if (userExists()) {

        }
        else {

        }
        //userName = null; //pull data and set username


        //load all the other user ids this user has with other users
            //todo code here
        //get a reference to each of the messages
            //eventually becomes a DatabaseReference list
            //remember to keep indexes for quicker access
        currentToUsers = database.getReference()
                .child("MessageDatabase")
                .child("University At Buffalo")//eventually will use a variable to replace this
                .child("placeholder").child("messages");
        usersToCurrent = database.getReference()
                .child("MessageDatabase")
                .child("University At Buffalo")//when users can input which university they belong to, edit this
                .child("placeholder").child("messages");

        /*new code end*/

        Button events = findViewById(R.id.events);
        events.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent toEventActivity = new Intent(context, EventActivity.class);
                startActivity(toEventActivity);
            }
        });
        Button nearMe = findViewById(R.id.nearMe);
        nearMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent toNearMeActivity = new Intent(context, NearbyActivity.class);
                startActivity(toNearMeActivity);
            }
        });
        findViewById(R.id.events_near_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(context).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });

            }
        });
    }

    public boolean userExists() {
        //return currentData.child(uid).equalTo(uid);
        return true;
    }

    public void editProfile(View view) {
        //Intent intent = new Intent(context, EditProfileActivity.class);
        //intent.putExtras(getIdentityBundle());
        //startActivity(intent);
        currentData.child(uid).setValue(new Current(email, userName, displayName, null,description));
    }
    public Bundle getIdentityBundle() {
        return null;
    }
}

