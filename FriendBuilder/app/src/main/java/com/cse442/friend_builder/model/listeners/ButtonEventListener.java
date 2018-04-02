package com.cse442.friend_builder.model.listeners;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cse442.friend_builder.EventActivity;
import com.cse442.friend_builder.LoginActivity;
import com.cse442.friend_builder.R;
import com.cse442.friend_builder.model.HostedEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by zheng on 3/25/2018.
 */

public class ButtonEventListener implements View.OnClickListener {

    private long timeStamp;
    private HostedEvent hostedEvent;
    private String key;
    private View dialogEventDetails;
    private String email;
    private DatabaseReference eventReference;
    private AlertDialog eventDetails;
    private EventActivity context;

    public ButtonEventListener(long timeStamp, HostedEvent hostedEvent, String key, View dialogEventDetails, String email,
                               DatabaseReference eventReference, AlertDialog eventDetails, EventActivity context) {
        this.timeStamp = timeStamp;
        this.hostedEvent = hostedEvent;
        this.key = key;
        this.dialogEventDetails = dialogEventDetails;
        this.email = email;
        this.eventReference = eventReference;
        this.eventDetails = eventDetails;
        this.context = context;
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

    @Override
    public void onClick(View v) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        String[] dateTime = sdf.format(new Date(timeStamp)).split(" ");

        TextView hostName = dialogEventDetails.findViewById(R.id.hostName);
        hostName.setText("Host: " + hostedEvent.getHostName());
        TextView eventName = dialogEventDetails.findViewById(R.id.eventName);
        eventName.setText("Event Name: " + hostedEvent.getName());
        TextView eventTheme = dialogEventDetails.findViewById(R.id.eventTheme);
        eventTheme.setText("Event Theme: " + hostedEvent.getTheme());
        TextView describeLocation = dialogEventDetails.findViewById(R.id.describeLocation);
        describeLocation.setText("Location Description: " + hostedEvent.getLocationDescription());
        TextView date = dialogEventDetails.findViewById(R.id.date);
        date.setText("Date Begin: " + dateTime[0]);
        TextView timeBegin = dialogEventDetails.findViewById(R.id.timeBegin);
        timeBegin.setText("Time Begin: " + dateTime[1]);
        TextView timeEnd = dialogEventDetails.findViewById(R.id.timeEnd);

        if (hostedEvent.getEnd() != null)
            timeEnd.setText("Time End: " + hostedEvent.getEnd());
        else timeEnd.setText("");
        TextView active = dialogEventDetails.findViewById(R.id.active);
        if (hostedEvent.isActive()) active.setText("Active");
        else active.setText("Inactive");

        Button end = dialogEventDetails.findViewById(R.id.end);
        if (!hostedEvent.isActive()) end.setVisibility(View.GONE);
        else end.setVisibility(View.VISIBLE);


        //Button end = dialogEventDetails.findViewById(R.id.endEvent);
        EndButtonListener eBL = new EndButtonListener(hostedEvent, timeStamp) {
            @Override
            public void onClick(View v) {
                Map map = new HashMap();
                map.put("timeEnd", ServerValue.TIMESTAMP);
                eventReference.child(removeInvalidKeyCharacters(email)).child("timeEnd").setValue(map);

                eventReference.child(removeInvalidKeyCharacters(email)).child("timeEnd").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map map = (HashMap) dataSnapshot.getValue();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                        endTime = sdf.format(new Date((long)map.get("timeEnd")));
                        Map m = new HashMap();
                        hostedEvent = new HostedEvent(hostedEvent.getHostName(), hostedEvent.getName(),
                                hostedEvent.getTheme(), null, null, endTime, false);
                        m.put("event", hostedEvent);
                        m.put("time", startTime);

                        eventReference.child(removeInvalidKeyCharacters(email)).child(key).setValue(m);
                        eventDetails.hide();
                        context.startActivity(new Intent(context, LoginActivity.class));
                        context.finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        end.setOnClickListener(eBL);
        eventDetails.show();
    }
}
