package com.cse442.friend_builder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.location.LocationManager;
import android.location.Location;
import android.widget.TextView;

import com.cse442.friend_builder.model.BrianDictionary;
import com.cse442.friend_builder.model.Current;
import com.cse442.friend_builder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.BiConsumer;

public class NearbyActivity extends AppCompatActivity {

    private TextView place;
    private Location userplace;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("User").child("University At Buffalo");
    Current other;
    String info;
    ArrayList<String> userlist = new ArrayList<String>();
    String[] real = {};
    String[] example = {};
    NearbyActivity context = this;
    boolean found = true;

    BrianDictionary others = new BrianDictionary();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        getSupportActionBar().setTitle("Nearby Users");

        // Acquire a reference to the system Location Manager
        LocationManager manager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener listener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                userplace = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        while (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }


        String loc = "";

        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        }
        catch(NullPointerException e)
        {
            loc = "Searching...";
        }


        String provider = LocationManager.GPS_PROVIDER;
        //String provider = LocationManager.NETWORK_PROVIDER;


        //Try to find the user's location

        try{
            userplace = manager.getLastKnownLocation(provider);
            loc = String.format("%.2f", userplace.getLatitude());
            loc = loc + ", " + String.format("%.2f", userplace.getLongitude());

            found = true;
        }
        catch (NullPointerException n)
        {
            loc = "Not Found";
            found = false;
        }

        place = (TextView) findViewById(R.id.place);

        place.setText(loc);

        //Send userplace to database
        //need instruction here

        Intent intent = getIntent();
        //String message = intent.getStringExtra(ProfileActivity.EXTRA_MESSAGE);


        //get users from database


        ref.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int count = 0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Intent intent = getIntent();


                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String myName = preferences.getString("myName", "");
                    String myEmail = removeInvalidKeyCharacters(preferences.getString("myEmail", ""));

                    System.out.println("##################");
                    System.out.println(snapshot);
                    System.out.println(snapshot.getValue());
                    other = snapshot.getValue(Current.class);
                    System.out.println(other.getName());

                    String email = removeInvalidKeyCharacters(other.getEmail());

                    String dist = "Undefined";
                    double distance = 0;

                    if (found) {
                        Location otherLocation = new Location(userplace);
                        otherLocation.setLongitude(other.getLon());
                        otherLocation.setLatitude(other.getLat());
                        distance = userplace.distanceTo(otherLocation) / 1609.34;
                        dist = String.format("%.2f", distance);
                    }

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if(! removeInvalidKeyCharacters(user.getEmail()).equals( email)) {

                        count = count + 1;
                        info = myName + "," + other.getName() + "," + dist + "," + email + "," + other.getDescription() + "," + other.getInterest0() + "," + other.getInterest1() + "," + other.getInterest2();


                        //Send to Dictionary

                        others.add(distance, info);
                    }

                    System.out.println("Mail " + removeInvalidKeyCharacters(user.getEmail()));
                    System.out.println("Email " + email);

                    //userlist.add(info);
                    System.out.println(userlist);
                    System.out.println("User List");
                    System.out.println(userlist);

                }

                others.setLength(count);
                userlist = others.make();

                try{
                    real = userlist.toArray(new String[count]);
                }
                catch(NullPointerException e)
                {
                    real = example;
                }

                    ListAdapter r = new NearbyUserAdapter(context, real);
                    ListView userlistview = (ListView) findViewById(R.id.userlistview);
                    userlistview.setAdapter(r);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(this,LoginActivity.class);
        startActivity(setIntent);
        finish();

    }
}
