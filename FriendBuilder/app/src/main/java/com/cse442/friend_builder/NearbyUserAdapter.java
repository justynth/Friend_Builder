package com.cse442.friend_builder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ActivityChooserView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Brian on 2/17/2018.
 */

class NearbyUserAdapter extends ArrayAdapter<String> {
    private Button b1;
    private int _position;
    NearbyUserAdapter(Context context, String[] users){

        super(context, R.layout.nearbyuserlist, users);
    }


    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater l = LayoutInflater.from(getContext());
        _position = position;
        View row = l.inflate(R.layout.nearbyuserlist, parent, false);

        String userall = getItem(position);
        //String[] user = new String[] {userall};
        final String[] user = userall.split(",");


        TextView u = (TextView) row.findViewById(R.id.username);
        TextView d = (TextView) row.findViewById(R.id.description);
        TextView i1 = (TextView) row.findViewById(R.id.interest1);
        TextView i2 = (TextView) row.findViewById(R.id.interest2);
        TextView i3 = (TextView) row.findViewById(R.id.interest3);
        TextView dis = (TextView) row.findViewById(R.id.distance);
        Button b1 = (Button) row.findViewById(R.id.button2);
        Button b2 = row.findViewById(R.id.button3);

        final String myName = user[0];
        u.setText(user[1]);
        d.setText(user[2]);
        i1.setText(user[3]);
        i2.setText(user[4]);
        i3.setText(user[5]);
        dis.setText(user[6]);
        final String email = user[7];

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),EventActivity.class);
                Bundle bundle = new Bundle();
                //Toast.makeText(getContext(), user[1], Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), email , Toast.LENGTH_SHORT).show();
                bundle.putString("name", user[1]);
                bundle.putString("user", email);
                bundle.putBoolean("current", false);
                intent.putExtra("user", bundle);
                view.getContext().startActivity(intent);
                ((Activity)view.getContext()).finish();
                /*name.putString("name", user[1]);
                intent.putExtra("name", name);
                intent.putExtra("email",email);
                intent.putExtra("myName",myName);
                view.getContext().startActivity(intent);
                ((Activity)view.getContext()).finish();*/
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),Message.class);
                Bundle name = new Bundle();
                name.putString("name", user[1]);
                intent.putExtra("name", name);
                intent.putExtra("email",email);
                intent.putExtra("myName",myName);
                view.getContext().startActivity(intent);
                ((Activity)view.getContext()).finish();
            }
        });
        return row;
    }
}






