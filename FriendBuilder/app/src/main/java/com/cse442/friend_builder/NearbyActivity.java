package com.cse442.friend_builder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.location.LocationManager;
import android.location.Location;
import android.widget.TextView;

public class NearbyActivity extends Activity {

    private TextView place;
    private Location userplace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

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


        //Try to find the user's location

        try{
            userplace = manager.getLastKnownLocation(provider);
            loc = Double.toString(userplace.getLatitude());
            loc = loc + ", " + Double.toString(userplace.getLongitude());
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

        String person1 = "Bob, I'm cool, Chess,Soccer,Jesus," + String.format("%.2f", bobdist);
        String person2 = "Jill,I like stuff,Running,Movies,Cats," + String.format("%.2f", jilldist);
        String person3 = "Clyde,Meet me!,Counting,Broadway,Sledding," + String.format("%.2f",clydedist);
        String[] example = new String[]{person1, person2, person3};


        ListAdapter l = new NearbyUserAdapter(this, example);
        ListView userlistview = (ListView) findViewById(R.id.userlistview);
        userlistview.setAdapter(l);
    }

}
