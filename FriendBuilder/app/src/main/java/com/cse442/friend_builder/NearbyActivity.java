package com.cse442.friend_builder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.preference.PreferenceManager;
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

import com.cse442.friend_builder.model.Current;
import com.cse442.friend_builder.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        String bob = "Undefined";
        String jill = "Undefined";
        String clyde = "Undefined";

        //Try to find the user's location

        try{
            userplace = manager.getLastKnownLocation(provider);
            loc = String.format("%.2f", userplace.getLatitude());
            loc = loc + ", " + String.format("%.2f", userplace.getLongitude());

            //Made up people with sample locations, to test the layout

            Location buffalo = new Location(userplace);
            buffalo.setLatitude(42.88);
            buffalo.setLongitude(-78.87);

            Location sanfran = new Location(userplace);
            sanfran.setLatitude(37.77);
            sanfran.setLongitude(-122.41);

            Location london = new Location(userplace);
            london.setLatitude(51.51);
            london.setLongitude(-.12);

            double bobdist = userplace.distanceTo(buffalo) / 1609.34;
            double jilldist = userplace.distanceTo(sanfran) / 1609.34;
            double clydedist = userplace.distanceTo(london) / 1609.34;

            bob = String.format("%.2f", bobdist);
            jill =  String.format("%.2f", jilldist);
            clyde = String.format("%.2f",clydedist);

        }
        catch (NullPointerException n)
        {
            loc = "Not Found";
        }

        place = (TextView) findViewById(R.id.place);

        place.setText(loc);

        //Send userplace to database
        //need instruction here

        Intent intent = getIntent();
        //String message = intent.getStringExtra(ProfileActivity.EXTRA_MESSAGE);


        String person1 = "Bob, I'm cool, Chess,Soccer,Jesus," + bob;
        String person2 = "Jill,I like stuff,Running,Movies,Cats," + jill;
        String person3 = "Clyde,Meet me!,Counting,Broadway,Sledding," + clyde;
        example = new String[]{person1, person2, person3};

        //get users from database


        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int count = 0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Intent intent = getIntent();


                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String myName = preferences.getString("myName", "");

                    System.out.println("##################");
                    System.out.println(snapshot);
                    System.out.println(snapshot.getValue());
                    other = snapshot.getValue(Current.class);
                    System.out.println(other.getName());

                    String email = removeInvalidKeyCharacters(other.getEmail());

                    Location otherLocation = new Location(userplace);
                    otherLocation.setLongitude(other.getLon());
                    otherLocation.setLatitude(other.getLat());
                    double distance = userplace.distanceTo(otherLocation) / 1609.34;
                    String dist = String.format("%.2f", distance);

                    count = count + 1;
                    info = myName + "," + other.getName() + "," + dist + "," + email + "," + other.getDescription() + "," + other.getInterest0() + "," + other.getInterest1() + "," + other.getInterest2();
                    userlist.add(info);
                    System.out.println(userlist);
                    System.out.println("User List");
                    System.out.println(userlist);


                    try {
                        real = userlist.toArray(new String[count]);
                    }
                    catch (NullPointerException e)
                    {
                        real = example;
                    }

                    System.out.println("Real");
                    System.out.println(real.toString());
                    System.out.println(example.toString());

                    //ListAdapter l = new NearbyUserAdapter(context, example);
                    ListAdapter r = new NearbyUserAdapter(context, real);
                    ListView userlistview = (ListView) findViewById(R.id.userlistview);
                    //userlistview.setAdapter(l);
                    userlistview.setAdapter(r);
                }

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
