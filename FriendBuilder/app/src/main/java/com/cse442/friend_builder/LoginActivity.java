package com.cse442.friend_builder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log; //New for authentication email

import com.cse442.friend_builder.model.Current;
import com.cse442.friend_builder.model.Event;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    /*new code*/
    private LoginActivity context;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase database;
    private DatabaseReference userNameReference;
    private DatabaseReference userReference;

    private String firebaseUid; //initialized by listener
    private String email; //initialized by listener
    private String name; //initialized by listener
    private Current currentUser;
    private boolean emailIsVerified;

    private TextView emailAuthMessage;
    private TextView description;
    private TextView nameView;
    private TextView editName;
    private TextView editDescription;
    private TextView interest0;
    private TextView interest1;
    private TextView interest2;
    private EditText editInterest0;
    private EditText editInterest1;
    private EditText editInterest2;

    private Button editProfile;
    private Button myEvents;
    private Button usersNearMe;
    private Button eventsNearMe;

    private Location userplace;
    private boolean found;
    private static final String TAG = "LoginActivity";


    private SharedPreferences editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        while (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

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

    private String removeComma(String key){
        StringBuilder answer = new StringBuilder();
        for(int i = 0;i<key.length(); ++i){
            char c = key.charAt(i);
            if(c != ','){
                answer.append(c);
            }

        }
        if(answer.length()<=0){
            answer.append("Hi");
        }
        return answer.toString();
    }

    private void getChangedName(){
        editor = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = editor.edit();
        edit.putString("myName", removeComma(nameView.getText().toString()));
        edit.commit();
    };

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
        userNameReference = database.getReference().child("UserNames").child("University At Buffalo");
        userReference = database.getReference().child("User").child("University At Buffalo");

        emailAuthMessage = findViewById(R.id.emailAuthMessage);
        description = findViewById(R.id.description);
        nameView = findViewById(R.id.username);
        editProfile = findViewById(R.id.editProfile);
        myEvents = findViewById(R.id.myEvents);
        usersNearMe = findViewById(R.id.usersNearMe);
        eventsNearMe = findViewById(R.id.eventsNearMe);
        editDescription = findViewById(R.id.editDescription);
        editName = findViewById(R.id.editName);
        interest0 = findViewById(R.id.i0);
        interest1 = findViewById(R.id.i1);
        interest2 = findViewById(R.id.i2);
        editInterest0 = findViewById(R.id.editInterest0);
        editInterest1 = findViewById(R.id.editInterest1);
        editInterest2 = findViewById(R.id.editInterest2);
    }

    private void setEverythingExceptPicAndName(int visibility) {
        findViewById(R.id.descriptionHeader).setVisibility(visibility);
        findViewById(R.id.interestsHeader).setVisibility(visibility);
        interest0.setVisibility(visibility);
        interest1.setVisibility(visibility);
        interest2.setVisibility(visibility);
        description.setVisibility(visibility);
        nameView.setVisibility(visibility);
        editProfile.setVisibility(visibility);
        myEvents.setVisibility(visibility);
        usersNearMe.setVisibility(visibility);
        eventsNearMe.setVisibility(visibility);
        emailAuthMessage.setVisibility(View.INVISIBLE);
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

                    emailIsVerified=user.isEmailVerified();

                    if(emailIsVerified){
                        emailAuthMessage.setVisibility(View.INVISIBLE); //Hide email auth message when authenticated
                        userReference.child(removeInvalidKeyCharacters(email)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Toast.makeText(context, ""+(dataSnapshot.exists()), Toast.LENGTH_SHORT).show();
                                if (!dataSnapshot.exists()) {
                                    //need user to sign up username



                                    LocationManager manager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
                                    String provider = LocationManager.GPS_PROVIDER;


                                    // Define a listener that responds to location updates
                                    LocationListener listener = new LocationListener() {
                                        public void onLocationChanged(Location location) {
                                            // Called when a new location is found by the network location provider.
                                            userplace = location;
                                        }

                                        public void onStatusChanged(String provider, int status, Bundle extras) {
                                        }

                                        public void onProviderEnabled(String provider) {
                                        }

                                        public void onProviderDisabled(String provider) {
                                        }
                                    };

                                    String loc = "";

                                    try {
                                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            return;
                                        }
                                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                                    }
                                    catch(NullPointerException e)
                                    {
                                        loc = "Searching...";
                                    }

                                    try{
                                        userplace = manager.getLastKnownLocation(provider);
                                        currentUser = new Current(email, name, "I have not customized my profile yet.", userplace.getLatitude(), userplace.getLongitude());
                                    }
                                    catch (NullPointerException n)
                                    {
                                        loc = "Not Found";
                                        found = false;
                                        currentUser = new Current(email, name, "I have not customized my profile yet and I have the default location.", 0, 0);
                                    }


                                    userReference.child(removeInvalidKeyCharacters(email)).setValue(currentUser);
                                    setEverythingExceptPicAndName(View.VISIBLE);
                                    //setUserNameCreation(View.VISIBLE);
                                } else {
                                    userReference.child(removeInvalidKeyCharacters(email)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            currentUser = dataSnapshot.getValue(Current.class);
                                            nameView.setText(currentUser.getName());
                                            description.setText(currentUser.getDescription());
                                            interest0.setText(currentUser.getInterest0());
                                            interest1.setText(currentUser.getInterest1());
                                            interest2.setText(currentUser.getInterest2());
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

                    }
                    else{
                        //Logged in but email is not verified
                        //Load the correct components (background + message + logoutButton


                        //Make email auth text box and logout button appear
                        emailAuthMessage.setVisibility(View.VISIBLE);
                        eventsNearMe.setVisibility(View.VISIBLE);




                        //Send email
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "You sent an email!");

                                }
                            }
                        });

                    }






                    //loadDataForCurrentUser();
                } else {
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
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description.setVisibility(View.GONE);
                findViewById(R.id.descriptionHeader).setVisibility(View.GONE);
                findViewById(R.id.interestsHeader).setVisibility(View.GONE);
                nameView.setVisibility(View.GONE);
                interest0.setVisibility(View.GONE);
                interest1.setVisibility(View.GONE);
                interest2.setVisibility(View.GONE);
                editDescription.setVisibility(View.VISIBLE);
                editName.setVisibility(View.VISIBLE);
                editName.setText(nameView.getText().toString());
                editInterest0.setVisibility(View.VISIBLE);
                editInterest1.setVisibility(View.VISIBLE);
                editInterest2.setVisibility(View.VISIBLE);
                editDescription.setText(description.getText().toString());
                editInterest0.setText(interest0.getText().toString());
                editInterest1.setText(interest1.getText().toString());
                editInterest2.setText(interest2.getText().toString());
                editProfile.setText("Save");
                editProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        description.setText(removeComma(editDescription.getText().toString()));
                        nameView.setText(removeComma(editName.getText().toString()));
                        ArrayList<Event> temp = new ArrayList<>();
                        //temp.add(new HostedEvent(userName, "SSB4", "Competition", null, null, null, false));

                        LocationManager manager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
                        String provider = LocationManager.GPS_PROVIDER;


                        // Define a listener that responds to location updates
                        LocationListener listener = new LocationListener() {
                            public void onLocationChanged(Location location) {
                                // Called when a new location is found by the network location provider.
                                userplace = location;
                            }

                            public void onStatusChanged(String provider, int status, Bundle extras) {
                            }

                            public void onProviderEnabled(String provider) {
                            }

                            public void onProviderDisabled(String provider) {
                            }
                        };

                        String loc = "";

                        try {
                            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                        }
                        catch(NullPointerException e)
                        {
                            loc = "Searching...";
                        }

                        try{
                            userplace = manager.getLastKnownLocation(provider);
                        }
                        catch (NullPointerException n)
                        {
                            loc = "Not Found";
                        }

                        currentUser = new Current(email, removeComma(editName.getText().toString()), removeComma(editDescription.getText().toString()), userplace.getLatitude(), userplace.getLongitude());
                        currentUser.setInterest0(removeComma(editInterest0.getText().toString()));
                        currentUser.setInterest1(removeComma(editInterest1.getText().toString()));
                        currentUser.setInterest2(removeComma(editInterest2.getText().toString()));
                        userReference.child(removeInvalidKeyCharacters(email)).setValue(currentUser);
                        editProfile.setText("edit");
                        description.setVisibility(View.VISIBLE);
                        findViewById(R.id.descriptionHeader).setVisibility(View.VISIBLE);
                        findViewById(R.id.interestsHeader).setVisibility(View.VISIBLE);
                        nameView.setVisibility(View.VISIBLE);
                        interest0.setVisibility(View.VISIBLE);
                        interest1.setVisibility(View.VISIBLE);
                        interest2.setVisibility(View.VISIBLE);
                        editName.setVisibility(View.GONE);
                        editDescription.setVisibility(View.GONE);
                        editInterest0.setVisibility(View.GONE);
                        editInterest1.setVisibility(View.GONE);
                        editInterest2.setVisibility(View.GONE);

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
                bundle.putString("name", currentUser.getName());
                bundle.putString("user", email);
                bundle.putBoolean("current", true);

                startActivity(intent);
                finish();
            }
        });

        usersNearMe.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent toNearMeActivity = new Intent(context, NearbyActivity.class);
                getChangedName();
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

    @Override
    public void onBackPressed() {
        if(editProfile.getText().toString()=="edit") {
            AuthUI.getInstance().signOut(context);
        }
    }
}

