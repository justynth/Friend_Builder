package com.cse442.friend_builder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class UserCreation extends AppCompatActivity {
    private EditText email,pass,cpass;
    private Button b1,b2;
    private TextView Info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_creation);
        email=(EditText)findViewById(R.id.user);
        pass=(EditText)findViewById(R.id.pass);
        cpass=(EditText)findViewById(R.id.cpassword);
        b1=(Button)findViewById(R.id.register);
        b2=(Button)findViewById(R.id.devclear);
        Info = (TextView)findViewById(R.id.info);
        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String s1 = email.getText().toString();
                String s2 = pass.getText().toString();
                String s3 = cpass.getText().toString();
                register(s1,s2,s3);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devClear();
            }
        });
    }

    private void devClear(){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.apply();
    }
    private void register(String userName,String userPassword,String confirmPassword){
        if(userPassword.equals(confirmPassword)){
            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String user = shared.getString(userName,"");
            if(user.equals("")){
                SharedPreferences.Editor editor = shared.edit();
                editor.putString(userName,userPassword);
                editor.apply();
                Intent intent = new Intent(UserCreation.this,LoginActivity.class);
                Info.setText("Register Successful");
                startActivity(intent);
            }else{
                Info.setText("Username taken");
            }
        }else{
            Info.setText("Password doesn't match");
        }
    }

}
