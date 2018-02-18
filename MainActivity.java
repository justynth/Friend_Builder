package com.example.brian.example;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Made up people, to test the layout

        String person1 = "Bob, I'm cool, Chess,Soccer,Jesus";
        String person2 = "Jill,I like stuff,Running,Movies,Cats";
        String person3 = "Clyde,Meet me!,Counting,Broadway,Sledding";
        String[] example = new String[]{person1, person2, person3};

        String[] users = {"Brian", "Ted", "Sam"};

        ListAdapter l = new NearbyUserAdapter(this, example);
        ListView userlistview = (ListView) findViewById(R.id.userlistview);
        userlistview.setAdapter(l);



    }
}
