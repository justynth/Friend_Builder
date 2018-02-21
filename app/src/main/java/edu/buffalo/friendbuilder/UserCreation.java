package edu.buffalo.friendbuilder;

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
        final Database data = new Database();
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
                register(s1,s2,s3,data);
            }
        });
    }

    private void validate(String userName,String userPassword, String confirmPassword){

    }
    private void register(String userName,String userPassword,String confirmPassword,Database data){
        if(userPassword.equals(confirmPassword)){
            Intent intent = new Intent(UserCreation.this,Profile.class);
            data.getMap().put(userName,userPassword);
            Info.setText("Register Successful");

        }else{
            Info.setText("Password doesn't match");
        }
    }

}
