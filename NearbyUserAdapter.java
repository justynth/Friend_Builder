package com.example.brian.example;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.ActivityChooserView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.brian.example.R;

import java.util.zip.Inflater;

/**
 * Created by Brian on 2/17/2018.
 */

class NearbyUserAdapter extends ArrayAdapter<String> {

    NearbyUserAdapter(Context context, String[] users){
        super(context, R.layout.nearbyuserlist, users);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater l = LayoutInflater.from(getContext());
        View row = l.inflate(R.layout.nearbyuserlist, parent, false);

        String userall = getItem(position);
        //String[] user = new String[] {userall};
        String[] user = userall.split(",");


        TextView u = (TextView) row.findViewById(R.id.username);
        TextView d = (TextView) row.findViewById(R.id.description);
        TextView i1 = (TextView) row.findViewById(R.id.interest1);
        TextView i2 = (TextView) row.findViewById(R.id.interest2);
        TextView i3 = (TextView) row.findViewById(R.id.interest3);

        u.setText(user[0]);
        d.setText(user[1]);
        i1.setText(user[2]);
        i2.setText(user[3]);
        i3.setText(user[4]);
        return row;
    }
}






