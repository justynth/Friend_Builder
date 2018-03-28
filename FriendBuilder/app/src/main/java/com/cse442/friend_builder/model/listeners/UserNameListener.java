package com.cse442.friend_builder.model.listeners;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

/**
 * Created by zheng on 3/22/2018.
 */

public class UserNameListener implements ChildEventListener {

    public ArrayList<String> existingUserNames = new ArrayList<>();

    public UserNameListener() {

    }

    public ArrayList<String> getExistingUserNames() {
        return existingUserNames;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Object str = dataSnapshot.child("userName").getValue();
                            /*if (str != null) {
                                Toast.makeText(context, str.toString(), Toast.LENGTH_SHORT).show();
                                if (str.toString().equals(decided)) {
                                    valid = false;
                                    Toast.makeText(context, "This is already taken", Toast.LENGTH_SHORT).show();
                                }
                            }*/
        if (str != null)
            existingUserNames.add(str.toString());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
