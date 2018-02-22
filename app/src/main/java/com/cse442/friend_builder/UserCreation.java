package com.cse442.friend_builder;

import android.content.Intent;
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
    private Button b1;
    private TextView Info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_creation);
        email=(EditText)findViewById(R.id.user);
        pass=(EditText)findViewById(R.id.pass);
        cpass=(EditText)findViewById(R.id.cpassword);
        b1=(Button)findViewById(R.id.register);
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
    }

    private void validate(String userName,String userPassword, String confirmPassword){

    }
    private void register(String userName,String userPassword,String confirmPassword){
        if(userPassword.equals(confirmPassword)){
            Intent intent = new Intent(UserCreation.this,LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(userName,userPassword);
            Info.setText("Register Successful");
            startActivity(intent);

        }else{
            Info.setText("Password doesn't match");
        }
    }

}
