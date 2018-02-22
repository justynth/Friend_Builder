package com.cse442.friend_builder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Message extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //the only button on the screen
        Button accessFriendProfile = findViewById(R.id.accessFriend);
        accessFriendProfile.setOnClickListener(this);

        //setting the name displayed on the button
        Intent connector = getIntent();
        Bundle savedNames = connector.getBundleExtra("name");
        if(savedNames != null) {
            accessFriendProfile.setText(savedNames.getString("name"));
        }
    }

    @Override
    public void onClick(View view) {
        Intent connector = new Intent(this, LoginActivity.class);
        startActivity(connector);

    }
}
