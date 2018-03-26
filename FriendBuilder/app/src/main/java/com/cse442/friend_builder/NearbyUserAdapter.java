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
        String[] user = userall.split(",");
        //String myName = intent.getExtras().get("myName").toString();

        TextView u = (TextView) row.findViewById(R.id.username);
        TextView d = (TextView) row.findViewById(R.id.description);
        TextView i1 = (TextView) row.findViewById(R.id.interest1);
        TextView i2 = (TextView) row.findViewById(R.id.interest2);
        TextView i3 = (TextView) row.findViewById(R.id.interest3);
        TextView dis = (TextView) row.findViewById(R.id.distance);
        Button b1 = (Button) row.findViewById(R.id.button2);
        final String myName = user[0];
        u.setText(user[1]);
        d.setText(user[2]);
        i1.setText(user[3]);
        i2.setText(user[4]);
        i3.setText(user[5]);
        dis.setText(user[6]);

        if (user[1].equals("Admin")) {
            b1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),Message.class);
                    Bundle name = new Bundle();

                    name.putString("name", "Admin");
                    intent.putExtra("name", name);
                    intent.putExtra("email","admin@gmailcom");
                    intent.putExtra("myName",myName);
                    view.getContext().startActivity(intent);
                }
            });
        }
        else if (user[1].equals("Peter")) {
            b1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),Message.class);
                    Bundle name = new Bundle();

                    name.putString("name", "Peter");
                    intent.putExtra("name", name);
                    intent.putExtra("email","pyang@buffaloedu");
                    intent.putExtra("myName",myName);
                    view.getContext().startActivity(intent);
                }
            });

        }
        else if (user[1].equals("Clyde")) {
            b1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),Message.class);
                    Bundle name = new Bundle();
                    name.putString("name", "Clyde");
                    intent.putExtra("name", name);
                    view.getContext().startActivity(intent);
                }
            });
        }
        else {}

        return row;
    }
}






