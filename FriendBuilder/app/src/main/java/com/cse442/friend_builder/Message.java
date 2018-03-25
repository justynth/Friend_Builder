package com.cse442.friend_builder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cse442.friend_builder.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Message extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference rootR;
    private FirebaseAuth mAuth;

    private String sender;
    private String reciever;

    private RecyclerView mList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdaptar messageAdaptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        rootR = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        sender = mAuth.getCurrentUser().getUid();
        Button accessFriendProfile = findViewById(R.id.accessFriend);
        accessFriendProfile.setOnClickListener(this);
        Intent connector = getIntent();
        Bundle savedNames = connector.getBundleExtra("name");
        reciever = connector.getExtras().get("uid").toString();
        final String myName = connector.getExtras().get("myName").toString();
        if(savedNames != null) {
            accessFriendProfile.setText(savedNames.getString("name"));
        }

        final EditText message = findViewById(R.id.message);
        ImageButton send = findViewById(R.id.send);

        messageAdaptar = new MessageAdaptar(messagesList);
        mList = (RecyclerView) findViewById(R.id.MessageList);

        linearLayoutManager = new LinearLayoutManager(this);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.setAdapter(messageAdaptar);

        Fetch();

        //setting the name displayed on the button


        rootR.child("User").child(reciever).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = message.getText().toString();
                if(TextUtils.isEmpty(messageText)){
                    Toast.makeText(Message.this,"No Message",Toast.LENGTH_SHORT);
                }else{
                    String reciever_ref = "Messages/" + sender +"/"+ reciever;
                    String sender_ref = "Messages/" + reciever +"/"+ sender;
                    DatabaseReference user_message = rootR.child("Messages").child(sender)
                            .child(reciever).push();
                    String message_id = user_message.getKey();

                    Map MessageBody = new HashMap();
                    MessageBody.put("message",messageText);
                    MessageBody.put("name",myName);
                    MessageBody.put("type","text");

                    Map MessageDetails = new HashMap();
                    MessageDetails.put(sender_ref+"/"+message_id,MessageBody);
                    MessageDetails.put(reciever_ref+"/"+message_id,MessageBody);

                    rootR.updateChildren(MessageDetails, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                           if(databaseError != null){
                               Log.d("Chat_log",databaseError.getMessage().toString());
                           }
                           message.setText("");
                        }
                    });

                }

            }
        });
    }

    private void Fetch() {
        rootR.child("Messages").child(sender).child(reciever).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdaptar.notifyDataSetChanged();
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
        });
    }

    @Override
    public void onClick(View view) {
        //Intent connector = new Intent(this, LoginActivity.class);
        //startActivity(connector);

    }
}
