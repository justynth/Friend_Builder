package com.cse442.friend_builder;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {
    private ProfileActivity current;
    private TextView myname,usedes;
    String DESCRIPTION = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        GetDescphp get = new GetDescphp();

        myname=(TextView)findViewById(R.id.Myname);
        usedes=(TextView)findViewById(R.id.ident);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        int userid = intent.getIntExtra("id",1);
        myname.setText(name);
        get.execute(String.valueOf(userid));
        //useid.setText(String.valueOf(userid));
        current = this;

        Button events = findViewById(R.id.events);
        events.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent toEventActivity = new Intent(current, EventActivity.class);
                startActivity(toEventActivity);
            }
        });
        Button nearMe = findViewById(R.id.nearMe);
        nearMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent toNearMeActivity = new Intent(current, NearbyActivity.class);
                startActivity(toNearMeActivity);
            }
        });

    }
    public void validate(String DESCRIPTION){
        usedes.setText(DESCRIPTION);
    }
    public void error(){
        usedes.setText("Error getting data");
    }
    class GetDescphp extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {

            String id = strings[0];
            String data = "";
            int tmp;
            try {
                URL url = new URL("https://friendbuilder.localtunnel.me/android/andriod.php");
                String urlParams = "id=" + id;

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
                        usedes.setText("Server is unavailable\nTry again later");
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

                DESCRIPTION = user_data.getString("description");

                if (!error) {
                    validate(DESCRIPTION);
                } else {
                    error();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                errr = "Exception: " + e.getMessage();
                error();

            }
        }
    }
}

