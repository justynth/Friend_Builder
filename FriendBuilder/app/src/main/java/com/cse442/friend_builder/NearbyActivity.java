package com.cse442.friend_builder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class NearbyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        Intent intent = getIntent();
        String message = intent.getStringExtra(ProfileActivity.EXTRA_MESSAGE);

        //Made up people, to test the layout

        String person1 = "Bob, I'm cool, Chess,Soccer,Jesus, 15";
        String person2 = "Jill,I like stuff,Running,Movies,Cats, 4";
        String person3 = "Clyde,Meet me!,Counting,Broadway,Sledding, 9";
        String[] example = new String[]{person1, person2, person3};

        String[] users = {"Brian", "Ted", "Sam"};

        ListAdapter l = new NearbyUserAdapter(this, example);
        ListView userlistview = (ListView) findViewById(R.id.userlistview);
        userlistview.setAdapter(l);
    }

}
