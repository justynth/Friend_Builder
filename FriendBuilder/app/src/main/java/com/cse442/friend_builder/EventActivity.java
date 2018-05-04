package com.cse442.friend_builder;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cse442.friend_builder.model.Event;
import com.cse442.friend_builder.model.HostedEvent;
import com.cse442.friend_builder.model.listeners.ButtonEventListener;
import com.cse442.friend_builder.model.listeners.EndButtonListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

public class EventActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static EventActivity context;

    private static LinearLayout hostedEventsList;
    private static LinearLayout attendedEventsList;
    private EditText eventName;
    private EditText eventTheme;
    private EditText describeLocation;
    private Button submit;
    private View dialogView;
    private View dialogEventDetails;
    private AlertDialog eventDetails;
    private AlertDialog dialog;

    private Hosted hosted;
    private Attended attended;

    private FirebaseDatabase database;
    private DatabaseReference eventReference;

    private String email;
    private String name;

    private boolean validInput(String s) {
        if (s.length() == 0) return false;
        int spaceCount = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ' ') spaceCount++;
        }
        if (spaceCount == s.length()) return false;
        return true;
    }

    private void initializeInstanceVariables() {
        context = this;

        email = getIntent().getBundleExtra("user").getString("user");
        name = getIntent().getBundleExtra("user").getString("name");

        database = FirebaseDatabase.getInstance();
        eventReference = database.getReference().child("Events").child("University At Buffalo");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        dialogView = getLayoutInflater().inflate(R.layout.dialog_create_event, null);
        alertDialogBuilder.setView(dialogView);
        dialog = alertDialogBuilder.create();

        dialogEventDetails = getLayoutInflater().inflate(R.layout.event_details, null);
        alertDialogBuilder.setView(dialogEventDetails);
        eventDetails = alertDialogBuilder.create();

        hosted = new Hosted();
        attended = new Attended();

        eventName = dialogView.findViewById(R.id.eventName);
        eventTheme = dialogView.findViewById(R.id.eventTheme);
        describeLocation = dialogView.findViewById(R.id.describeLocation);
        submit = dialogView.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validInput(eventName.getText().toString()) && validInput(eventTheme.getText().toString()) &&
                        validInput(describeLocation.getText().toString())) {
                    HostedEvent hostedEvent = new HostedEvent(
                            name + "/" + email, eventName.getText().toString(), eventTheme.getText().toString(), null, null, null, true
                    );

                    hostedEvent.setLocationDescription(describeLocation.getText().toString());

                    Map map = new HashMap();
                    map.put("time", ServerValue.TIMESTAMP);
                    map.put("event", hostedEvent);

                    eventReference.child(removeInvalidKeyCharacters(email)).push().setValue(map);

                    eventName.setText("");
                    eventTheme.setText("");
                    describeLocation.setText("");

                    dialog.hide();
                }
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



    private void addEventListener() {
        //this belong somewhere else that reads from the database
                eventReference.child(removeInvalidKeyCharacters(email)).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getKey().equals("timeEnd")) {}
                        else {
                            Button temp = new Button(context);
                            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                            HostedEvent hE = it.next().getValue(HostedEvent.class);
                            //Toast.makeText(context, ""+it.next().getValue().toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, ""+dataSnapshot.getChildren().iterator().next().getValue().toString(), Toast.LENGTH_SHORT).show();
                            //HostedEvent hE = (HostedEvent) m.get("event");
                            String key = dataSnapshot.getKey();
                            temp.setText(hE.getName());
                            ButtonEventListener bEL =  new ButtonEventListener((long)it.next().getValue(), hE, key) { //get this bEL and set different hostedEvent
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
                                                    startActivity(new Intent(context, LoginActivity.class));
                                                    finish();
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
                            };
                            temp.setOnClickListener(bEL);
                            hosted.hostedEventsList.addView(temp);
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        //Toast.makeText(EventActivity.this, ""+dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                       // if (!dataSnapshot.getKey().equals("timeEnd")) {
                        // }


                        /*HostedEvent hostedEvent = (HostedEvent) dataSnapshot.getValue();
                        TextView timeEnd = dialogEventDetails.findViewById(R.id.timeEnd);
                        if (hostedEvent.getEnd() != null)
                            timeEnd.setText("Time End: " + hostedEvent.getEnd());
                        else timeEnd.setText("");
                        TextView active = dialogEventDetails.findViewById(R.id.active);
                        if (hostedEvent.isActive()) active.setText("Active");
                        else active.setText("Inactive");*/

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
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);







        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        editVisuals();
        initializeInstanceVariables() ;
        addEventListener();



    }

    public void editVisuals() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Events");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (getIntent().getBundleExtra("user").getBoolean("current") == true)
            getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));





            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);


        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return hosted;
            else if (position == 1) return attended;

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(this,LoginActivity.class);
        startActivity(setIntent);
        finish();
    }
}
