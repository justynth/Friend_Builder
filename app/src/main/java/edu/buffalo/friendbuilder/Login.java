package edu.buffalo.friendbuilder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {
    private TextView err,SignUp;
    private EditText user,pass;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Database data = new Database();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.user);
        pass = (EditText)findViewById(R.id.pass);
        login = (Button)findViewById(R.id.login);
        err = (TextView)findViewById(R.id.err);
        SignUp = (TextView)findViewById(R.id.SignUp);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String s1 = user.getText().toString();
                String s2 = pass.getText().toString();
                validate(s1,s2);
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(Login.this,UserCreation.class);
            startActivity(intent);
        }
     });
    }

    private void validate(String u,String p){
        if(u.equals("Admin") && p.equals("Admin")){
            Intent intent = new Intent(Login.this,Profile.class);
            startActivity(intent);
        }else{
            err.setText("Email and Password do not match");
           // err.setText(data.getMap().get(u));
        }
    }
}
