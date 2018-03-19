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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DescActivity extends AppCompatActivity {
    private TextView des;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final int id = intent.getIntExtra("id",0);
        final String name = intent.getStringExtra("name");
        setContentView(R.layout.activity_desc);
        des = (TextView)findViewById(R.id.descri);
        Button desc = findViewById(R.id.done);
        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateDescphp update = new UpdateDescphp();
                update.execute(String.valueOf(id),des.getText().toString());
                Intent toProfile = new Intent(DescActivity.this, ProfileActivity.class);
                toProfile.putExtra("id", id);
                toProfile.putExtra("name",name);
                startActivity(toProfile);
            }
        });
    }

    class UpdateDescphp extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {

            String id = strings[0];
            String des = strings[1];
            String data = "";
            int tmp;
            try {
                URL url = new URL("https://friendbuilder.localtunnel.me/android/andriod_save.php");
                String urlParams = "id=" + id+ "&description=" + des;

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

            } catch (JSONException e) {
                e.printStackTrace();
                errr = "Exception: " + e.getMessage();
            }
        }
    }
}

