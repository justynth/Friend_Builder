package com.cse442.friend_builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Brian on 2/21/2018.
 */

public class ProfileActivity extends Activity {


    public static final String EXTRA_MESSAGE = "com.cse442.friendbuilder.MESSAGE";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
    }

    public void sendMessage(View view) {

        Intent intent = new Intent(this, NearbyActivity.class);
        String message = "hi";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
