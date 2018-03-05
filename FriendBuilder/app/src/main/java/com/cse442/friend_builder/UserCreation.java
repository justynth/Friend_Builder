package com.cse442.friend_builder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    private boolean isPasswordValid(String s){
        return s.length()> 4;
    }

    private boolean isEmailValid(String e){
        if(e.contains("@")) {
            return true;
        }
        return false;
    }

    private void devClear(){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.apply();
    }
    private void register(String userName,String userPassword,String confirmPassword){
        if(userPassword.equals(confirmPassword)){
            //SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //String user = shared.getString(userName,"");
            if(isPasswordValid(userPassword)) {
                //SharedPreferences.Editor editor = shared.edit();
                //editor.putString(userName, userPassword);
                //editor.apply();
                //Intent intent = new Intent(UserCreation.this, LoginActivity.class);
                registerPhp register = new registerPhp();
                register.execute(userName,userPassword);
                Info.setText("registered!");
                //startActivity(intent);
            }else{
                Info.setText("Invalid Password");}
        }else{
            Info.setText("Password doesn't match");
        }
    }

    public void usertaken(){
        Info.setText("Username has been taken");
    }
    public void erroroccured(){
        Info.setText("some took my pancakes");
    }
    public void wtf(){
        Info.setText("the overall happiness has increased");
    }


    class registerPhp extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            String data="";
            int tmp;

            try {
                URL url = new URL("http://10.84.164.5/android/register_info.php");
                String urlParams = "name="+""+"&email="+email+"&password="+password;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();
                InputStream is = httpURLConnection.getInputStream();
                while((tmp=is.read())!=-1){
                    data+= (char)tmp;
                }
                is.close();
                httpURLConnection.disconnect();

                return data;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject root = new JSONObject(s);
                boolean error = root.getBoolean("error");
                String msg = root.getString("error_msg");
                if(msg.equals("Unknown error occurred in registration!")){
                    wtf();
                }
                else if(msg.equals("Required parameters (name, email or password) is missing!")){
                    erroroccured();
                }else{
                    usertaken();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
