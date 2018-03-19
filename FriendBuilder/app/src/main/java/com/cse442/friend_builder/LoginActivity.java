package com.cse442.friend_builder;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;




import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private TextView err, SignUp;
    private EditText user, pass;
    private Button login;

    String NAME = null;
    String PASSWORD = null;
    String EMAIL = null;
    int ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);
        login = (Button) findViewById(R.id.login);
        err = (TextView) findViewById(R.id.err);
        SignUp = (TextView) findViewById(R.id.SignUp);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = user.getText().toString();
                String s2 = pass.getText().toString();
                login(s1, s2);
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UserCreation.class);
                startActivity(intent);
            }
        });
    }

    private void login(String u,String p){
        Loginphp l = new Loginphp();
        err.setText("Logging in");
        l.execute(u,p);
    }
    public void validate() {
        if(!err.getText().equals("Server is unavailable\nTry again later")){
            err.setText("Email and Password do not match");
        }
        // err.setText(data.getMap().get(u));
    }



    class Loginphp extends AsyncTask<String,String,String> {

            @Override
            protected String doInBackground(String... strings) {

                String email = strings[0];
                String password = strings[1];
                String data = "";
                int tmp;
                try {
                    URL url = new URL("https://friendbuilder.localtunnel.me/android/get_info.php");
                    String urlParams = "email=" + email + "&password=" + password;

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    OutputStream os = httpURLConnection.getOutputStream();
                    os.write(urlParams.getBytes());
                    os.flush();
                    os.close();

                    InputStream is = httpURLConnection.getInputStream();
                    while ((tmp = is.read()) != -1) {
                        data += (char) tmp;
                    }
                    is.close();
                    httpURLConnection.disconnect();
                    return data;
                } catch (MalformedURLException e) {
                    e.printStackTrace();

                    return "Exception: " + e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            err.setText("Server is unavailable\nTry again later");
                        }
                    });

                    return "Exception: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String s) {
                String errr = null;
                try {

                    JSONObject root = new JSONObject(s);
                    boolean error = root.getBoolean("error");


                    JSONObject user_data = root.getJSONObject("user");
                    ID = user_data.getInt("id");
                    NAME = user_data.getString("name");
                    EMAIL = user_data.getString("email");
                    if (!error) {
                        Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                        // i.putExtra("name",NAME);
                        i.putExtra("id", ID);
                        i.putExtra("name", NAME);
                        i.putExtra("email", EMAIL);
                        i.putExtra("err", errr);
                        startActivity(i);
                    } else {

                        validate();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errr = "Exception: " + e.getMessage();
                    validate();

                }
            }
        }
    }

