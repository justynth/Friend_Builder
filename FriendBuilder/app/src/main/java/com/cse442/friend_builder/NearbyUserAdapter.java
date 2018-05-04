package com.cse442.friend_builder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ActivityChooserView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


/**
 * Created by Brian on 2/17/2018.
 */

class NearbyUserAdapter extends ArrayAdapter<String> {
    private Button b1;
    private int _position;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Uri filePath;
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
        final TextView d = (TextView) row.findViewById(R.id.description);
        final TextView i1 = (TextView) row.findViewById(R.id.interest1);
        final TextView i2 = (TextView) row.findViewById(R.id.interest2);
        final TextView i3 = (TextView) row.findViewById(R.id.interest3);
        final TextView dis = (TextView) row.findViewById(R.id.distance);
        Button b1 = (Button) row.findViewById(R.id.button2);
        Button b2 = row.findViewById(R.id.button3);
        final ImageView pic = row.findViewById(R.id.otherpic);
        final ImageView bigpic = row.findViewById(R.id.bigpic);

        final String myName = user[0];
        u.setText(user[1]);
        dis.setText(user[2]);
        final String email = user[3];
        d.setText(user[4]);
        i1.setText(user[5]);
        i2.setText(user[6]);
        i3.setText(user[7]);

        String path = "profile/" + email  +".png";
        StorageReference storageRef = storage.getReference();
        storageRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(125,125).centerCrop().into(pic);
                Picasso.get().load(uri).resize(500,500).centerCrop().into(bigpic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any error
                String path = "https://firebasestorage.googleapis.com/v0/b/friendbuilder-336e6.appspot.com/o/profile%2Femptyprofile.png?alt=media&token=2d8a834b-64c7-490b-985b-ae932f134c33";
                Picasso.get().load(path).resize(125,125).centerCrop().into(pic);
                Picasso.get().load(path).resize(500,500).centerCrop().into(bigpic);
            }
        });


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

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                d.setVisibility(View.GONE);
                i1.setVisibility(View.GONE);
                i2.setVisibility(View.GONE);
                i3.setVisibility(View.GONE);
                pic.setVisibility(View.GONE);
                bigpic.setVisibility(View.VISIBLE);

                bigpic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bigpic.setVisibility(View.GONE);
                        d.setVisibility(View.VISIBLE);
                        i1.setVisibility(View.VISIBLE);
                        i2.setVisibility(View.VISIBLE);
                        i3.setVisibility(View.VISIBLE);
                        pic.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        return row;
    }
}






