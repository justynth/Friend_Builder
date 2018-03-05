package com.cse442.friend_builder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {
    private ProfileActivity current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        current = this;

        Button events = findViewById(R.id.events);
        events.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent toEventActivity = new Intent(current, EventActivity.class);
                startActivity(toEventActivity);
            }
        });
        Button nearMe = findViewById(R.id.nearMe);
        nearMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent toNearMeActivity = new Intent(current, NearbyActivity.class);
                startActivity(toNearMeActivity);
            }
        });
    }
}

